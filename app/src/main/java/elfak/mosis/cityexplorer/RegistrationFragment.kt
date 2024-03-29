package elfak.mosis.cityexplorer

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import android.Manifest
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import elfak.mosis.cityexplorer.R.layout.fragment_registration
import elfak.mosis.cityexplorer.databinding.FragmentRegistrationBinding
import java.io.ByteArrayOutputStream



class RegistrationFragment : Fragment() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val GALLERY_PERMISSION_REQUEST_CODE = 1002
    lateinit var editTextUsername: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonReg: Button
    lateinit var auth: FirebaseAuth
    lateinit var textView: TextView
    lateinit var editTextFirstName: EditText
    lateinit var editTextLastName: EditText
    lateinit var editTextPhoneNumber: EditText
    lateinit var progress: ProgressBar
    lateinit var database: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var imgUrl: String = ""
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var user: UserData
    private val sharedViewModel: UserViewModel by activityViewModels()

    private lateinit var openCameraButton: Button
    private lateinit var openGalleryButton: Button
    private lateinit var imageView: ImageView
    private lateinit var pictureReg: ProgressBar

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(fragment_registration, container, false)
        editTextUsername = view.findViewById(R.id.username)
        editTextPassword = view.findViewById(R.id.password)
        buttonReg = view.findViewById(R.id.register_button)
        textView = view.findViewById(R.id.loginNow)
        editTextPhoneNumber = view.findViewById(R.id.phoneNumber)
        editTextFirstName = view.findViewById(R.id.firstName)
        editTextLastName = view.findViewById(R.id.lastName)
        progress = view.findViewById(R.id.progressBar1)
        openCameraButton = view.findViewById(R.id.buttonCamera)
        openGalleryButton = view.findViewById(R.id.buttonGallery)
        imageView = view.findViewById(R.id.imageView6)
        storageRef = FirebaseStorage.getInstance().reference
        pictureReg = view.findViewById(R.id.PictureReg)



        auth = FirebaseAuth.getInstance()

        openCameraButton.setOnClickListener{
            if (checkCameraPermission()) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }
        openGalleryButton.setOnClickListener{
            if (checkGalleryPermission()) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY_PERMISSION_REQUEST_CODE)
            } else {
                // Ako dozvola nije odobrena, zahtevajte je
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    GALLERY_PERMISSION_REQUEST_CODE
                )
            }
        }

        textView.setOnClickListener {
            findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)
        }

        buttonReg.setOnClickListener {
            progress.visibility = View.VISIBLE
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            val phoneNumber = editTextPhoneNumber.text.toString()
            val firstName = editTextFirstName.text.toString()
            val lastName = editTextLastName.text.toString()




            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        progress.visibility = View.GONE
                        database= FirebaseDatabase.getInstance().getReference("Users")
                        user = UserData(username, password, phoneNumber, firstName, lastName, imgUrl, ArrayList(), 0 )
                        val key = username.replace(".", " ").replace("#","").replace("$", "").replace("[", "").replace("]", "")
                        database.child(key).setValue(user).addOnSuccessListener {
                            Log.d("ImageUrl", "Image URL: $imgUrl")
                            sharedViewModel.name = username
                            editTextUsername.text.clear()
                            editTextFirstName.text.clear()
                            editTextLastName.text.clear()
                            editTextPassword.text.clear()
                            editTextPhoneNumber.text.clear()
                        }
                        // Ako je kreiranje korisnika uspešno, možemo automatski navigirati korisnika na LoginFragment
                        findNavController().navigate(R.id.action_RegistrationFragment_to_LoginFragment)

                        // Takođe možete prikazati odgovarajuću poruku korisniku ako je potrebno
                        // Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                    } else {
                        // Ako kreiranje korisnika nije uspelo, prikaži odgovarajuću poruku o grešci korisniku
                        Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
    private fun checkCameraPermission() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun checkGalleryPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            //ZA SETOVANJE IMAGE VIEW-A
            imageView.setImageBitmap(imageBitmap)
            sendPicutreToFirestorageDownloadURLSendToRealtimeDatabase(imageBitmap)
        }
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Ovde obrada rezultata odabira slike iz galerije
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                // Očitavanje slike iz URI i postavljanje u ImageView
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    selectedImageUri
                )
                imageView.setImageBitmap(imageBitmap)

                // Otpremanje slike na Firebase Storage
                sendPicutreToFirestorageDownloadURLSendToRealtimeDatabase(imageBitmap)

            }
        }
    }
    private fun sendPicutreToFirestorageDownloadURLSendToRealtimeDatabase(imageBitmap:Bitmap)
    {
        val imagesRef = DataBase.storageRef.child("images/${System.currentTimeMillis()}.jpg")
        imageView.visibility=View.GONE
        pictureReg.visibility=View.VISIBLE
        // Convert the bitmap to bytes
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        // Upload the image to Firebase Storage
        val uploadTask = imagesRef.putBytes(imageData)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image upload success
                // Now you can get the download URL of the image and save it to the database
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    // Save the URI to the database or use it as needed
                    imgUrl = uri.toString()
                    imageView.visibility=View.GONE
                    imageView.visibility=View.VISIBLE
                    Toast.makeText(context,"Picture saved",Toast.LENGTH_SHORT).show()
                    // Add the code to save the URL to the user's data in Firebase Database here
                }.addOnFailureListener { exception ->
                    // Handle any errors that may occur while retrieving the download URL
                    Toast.makeText(
                        requireContext(),
                        "Failed to get download URL.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Image upload failed
                val errorMessage = task.exception?.message
                Toast.makeText(
                    requireContext(),
                    "Image upload failed. Error: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}
