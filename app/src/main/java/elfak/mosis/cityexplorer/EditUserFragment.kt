package elfak.mosis.cityexplorer

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import elfak.mosis.cityexplorer.data.UserData
import elfak.mosis.cityexplorer.model.UserViewModel
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream


class EditUserFragment : Fragment() {

    lateinit var pass: EditText
    lateinit var userName: EditText
    lateinit var firstname: EditText
    lateinit var lastname: EditText
    lateinit var phonenumber: EditText
    lateinit var progress: ProgressBar
    lateinit var button: Button
    lateinit var back:Button
    lateinit var database: DatabaseReference
    var imgUrl: String = ""
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var storageRef: StorageReference
    lateinit var user: UserData
    private  val sharedViewModel: UserViewModel by activityViewModels()
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    private lateinit var openCameraButton: Button
    private lateinit var imageView: ImageView
    private lateinit var picprogress: ProgressBar
    private lateinit var newPassQ: CheckBox
    private lateinit var new1: EditText
    private lateinit var new2: EditText
    var change=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_user, container, false)
        //INICIJALIZACIJA
        pass=view.findViewById(R.id.editTextOldPassU)
        userName=view.findViewById(R.id.editTextMailU)
        userName.setText(sharedViewModel.user.username)
        firstname=view.findViewById(R.id.editTextFNameU)
        firstname.setText(sharedViewModel.user.firstName)
        lastname=view.findViewById(R.id.editTextLNameU)
        lastname.setText(sharedViewModel.user.lastName)
        phonenumber=view.findViewById(R.id.editTextPhoneNumU)
        phonenumber.setText(sharedViewModel.user.phoneNumber.toString())
        progress=view.findViewById(R.id.progressBar1U)
        button=view.findViewById(R.id.buttonRegU)
        var auth= FirebaseAuth.getInstance().currentUser
        openCameraButton = view.findViewById(R.id.buttonPhotoU)
        imageView = view.findViewById(R.id.imageView6U)
        picprogress=view.findViewById(R.id.picprogress)
        newPassQ=view.findViewById(R.id.checkBox)
        var changeIt=false
        new1=view.findViewById(R.id.editTextPass1)
        new1.visibility=View.GONE
        new2=view.findViewById(R.id.editTextPass2)
        new2.visibility=View.GONE
        back=view.findViewById(R.id.buttonBackU)
        back.setOnClickListener{
            findNavController().navigate(R.id.action_EditUserFragment_to_HomeFragment)
        }
        newPassQ.setOnCheckedChangeListener{_,isChecked->
            if(isChecked)
            {
                changeIt=true
                new1.visibility=View.VISIBLE
                new2.visibility=View.VISIBLE
            }
            else
            {
                changeIt=false
                new1.visibility=View.GONE
                new2.visibility=View.GONE
            }
        }
        if(sharedViewModel.user.imageUrl!="")
        {
            Glide.with(requireContext())
                .load(sharedViewModel.user.imageUrl)
                .into(imageView)
            imgUrl=sharedViewModel.user.imageUrl.toString()
        }
        storageRef = FirebaseStorage.getInstance().reference
        // KLIK NA DUGME I UZ DOZVOLU POKRETANJE KAMERE
        openCameraButton.setOnClickListener{
            if (checkCameraPermission()) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            } else {
                // Ako dozvola nije odobrena, zahtevajte je
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }
        database = FirebaseDatabase.getInstance().getReference("Users")
        //KLIK NA AZURIRAJ DUGME AZURIRA PODATKE U BAZI, UPISUJE IH U SHARED VIEW MODEL I SALJE KORISNIKA NA FRAGMENT LICNE INFORMACIJE
        button.setOnClickListener {
            progress.visibility = View.VISIBLE
            var user1 = userName.text.toString()
            var pass1 = pass.text.toString()
            var name = firstname.text.toString()
            var surname = lastname.text.toString()
            var numberPhone = phonenumber.text.toString()
            user=UserData(user1,pass1,name,surname,numberPhone, imgUrl ,
                ArrayList(),sharedViewModel.user.points
            )
            //AZURIRANJE SPOREDNIH(NE KLJUCNIH ATRIBUTA)
            if(user1==sharedViewModel.user.username&&pass.text.toString()==sharedViewModel.user.password)
            {
                if(changeIt==false) {
                    updateRealTimeDataBase(user)
                }
                else if(changeIt==true)
                {
                    if(new1.text.toString()==new2.text.toString()&&new1.text.toString().length>5)
                    {
                        auth?.updatePassword(new1.text.toString())?.addOnCompleteListener{
                            user.username=new1.text.toString()
                            updateRealTimeDataBase(user)
                        }?.addOnFailureListener{exception-> Toast.makeText(context,exception.toString(),Toast.LENGTH_LONG).show()}
                    }
                    else
                    {
                        if(new1.text.toString()!=new2.text.toString())
                        {
                            Toast.makeText(context,"Passwords need to match",Toast.LENGTH_SHORT).show()
                        }
                        else
                        {
                            Toast.makeText(context,"Need stronger password",Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
            else
            {
                if(pass.text.toString().isEmpty()||pass.text.toString()!=user.username.toString())
                {
                    progress.visibility=View.GONE

                    Toast.makeText(context,"Need correct current password to update",Toast.LENGTH_SHORT).show()
                }



            }


        }



        return view
    }
    private fun updateRealTimeDataBase(user:UserData)
    {
        if(user.username!=null)
        {
            val key = user.username!!.replace(".", "").replace("#", "").replace("$", "").replace("[", "").replace("]", "")
            val userUpdates = mapOf(
                key to mapOf(
                    "username" to user.username,
                    "password" to user.password,
                    "firstName" to user.firstName,
                    "lastName" to user.lastName,
                    "phoneNumber" to user.phoneNumber,
                    "imageUrl" to user.imageUrl,
                    "points" to user.points
                )
            )
            database.updateChildren(userUpdates).addOnSuccessListener {
                progress.visibility=View.GONE
                sharedViewModel.name= user.username!!
                firstname.text.clear()
                lastname.text.clear()
                pass.text.clear()
                phonenumber.text.clear()
                firstname.text.clear()
                Toast.makeText(context,"Update successfull",Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_EditUserFragment_to_HomeFragment)

            }.addOnFailureListener{
                progress.visibility=View.GONE

                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun checkCameraPermission() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            val imagesRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()

            picprogress.visibility=View.VISIBLE
            imageView.visibility=View.GONE
            val uploadTask = imagesRef.putBytes(imageData)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imagesRef.downloadUrl.addOnSuccessListener { uri ->
                        imgUrl = uri.toString()
                        sharedViewModel.imageUrl=imgUrl
                        picprogress.visibility=View.GONE
                        imageView.visibility=View.VISIBLE
                    }.addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Failed to get download URL.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Image upload failed
                    val errorMessage = task.exception?.message
                    Toast.makeText(requireContext(), "Image upload failed. Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}


