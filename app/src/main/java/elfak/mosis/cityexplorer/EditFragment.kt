package elfak.mosis.cityexplorer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import java.io.ByteArrayOutputStream
import java.util.Calendar


class EditFragment : Fragment() {
    private  var imgUrl:String=""
    private val REQUEST_IMAGE_CAPTURE = 1
    val GALLERY_PERMISSION_REQUEST_CODE = 1002
    val CAMERA_PERMISSION_REQUEST_CODE = 1001



    private  val sharedViewModel: UserViewModel by activityViewModels()

    lateinit var placeName:EditText
    lateinit var description:EditText
    lateinit var grade:EditText
    lateinit var type: EditText
    lateinit var progress:ProgressBar
    lateinit var save:Button
    lateinit var cancel:Button
    var place: MyPlaces = MyPlaces()
    lateinit var latitude:EditText
    lateinit var longitude:EditText
    lateinit var set:Button
    lateinit var picture:ImageView
    ////////////////////////////////////////
    lateinit var pictureWait:ProgressBar
    private val locationViewModel: LocationViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_edit,container,false)

        picture=view.findViewById(R.id.objectPic)
        picture.visibility=View.VISIBLE
        placeName=view.findViewById(R.id.editmyplace_name_edit)
        description=view.findViewById(R.id.editmyplace_desc_edit)
        type = view.findViewById((R.id.editTextType))
        grade=view.findViewById(R.id.editmyplace_grade_edit)
        progress=view.findViewById(R.id.progressPlace)
        pictureWait=view.findViewById(R.id.pictureWait)
        save=view.findViewById(R.id.editmyplace_save_button)
        cancel=view.findViewById(R.id.editmyplace_cancel_button)
        latitude=view.findViewById(R.id.editmyplace_latitude_edit)
        longitude=view.findViewById(R.id.editmyplace_longitude_edit)



        val lonObserver= Observer<String>{newValue->
            longitude.setText(newValue.toString())
            sharedViewModel.longitude=longitude.text.toString()


        }
        locationViewModel.longitude.observe(viewLifecycleOwner,lonObserver)
        val latiObserver= Observer<String>{newValue->
            latitude.setText(newValue.toString())
            sharedViewModel.latitude=latitude.text.toString()

        }
        locationViewModel.latitude.observe(viewLifecycleOwner,latiObserver)
        set=view.findViewById(R.id.editmyplace_location_button)
        set.setOnClickListener{
            locationViewModel.addObject=true
            locationViewModel.viewObject=false
            locationViewModel.oneObject=false

            findNavController().navigate(R.id.action_EditFragment_to_MapFragment)

        }
        ///////////////////////////////////////////////////////////////////////////////
        save.setOnClickListener {
            if(sharedViewModel.lastLatitude==latitude.text.toString()&&sharedViewModel.lastLongitude==longitude.text.toString())
            {
                Toast.makeText(context,"Object already added on this location",Toast.LENGTH_SHORT).show()
            }
            else {

                val namePom = placeName.text.toString()
                var descPom = description.text.toString()
                val gradePom = grade.text.toString()
                if ((namePom.isNotEmpty() && gradePom.isNotEmpty() && gradePom.toInt() >= 5 && gradePom.toInt() <= 10) || (namePom.isNotEmpty() && gradePom.isNotEmpty() && gradePom.toInt() < 5 && gradePom.toInt() >= 1 && descPom.isNotEmpty())) {
                    var instance = Calendar.getInstance()
                    var month = instance.get(Calendar.MONTH).toInt() + 1
                    var date = instance.get(Calendar.DAY_OF_MONTH)
                        .toString() + "." + month.toString() + "." + instance.get(Calendar.YEAR)
                    var time = instance.get(Calendar.HOUR_OF_DAY).toString() + ":" + instance.get(
                        Calendar.MINUTE
                    )
                    var dateTime = date + " at " + time
                    progress.visibility = View.VISIBLE
                    if (description.text.toString().isEmpty()) {
                        descPom = ""
                    }
                    place = MyPlaces(
                        name = placeName.text.toString(),
                        comment = descPom,
                        grade = grade.text.toString(),
                        type = type.text.toString(),
                        img = imgUrl,
                        datetime = dateTime,
                        author = sharedViewModel.name,
                        longitude = longitude.text.toString(),
                        latitude = latitude.text.toString()
                    )


                    val key = placeName.text.toString().replace(".", "").replace("#", "")
                        .replace("$", "").replace("[", "").replace("]", "")

                    DataBase.databasePlaces.child(key).setValue(place)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                sharedViewModel.lastLatitude = latitude.text.toString()
                                sharedViewModel.lastLongitude = longitude.text.toString()
                                progress.visibility = View.GONE
                                Toast.makeText(
                                    context,
                                    "Place added ${placeName.text.toString()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                placeName.text.clear()
                                description.text.clear()
                                grade.text.clear()
                                longitude.setText("")
                                latitude.setText("")
                                picture.visibility = View.GONE
                                DataBase.databaseUsers.child(
                                    sharedViewModel.name.replace(".", "").replace("#", "")
                                        .replace("$", "").replace("[", "").replace("]", "")
                                ).get().addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        sharedViewModel.user = UserData(
                                            snapshot.child("username").value.toString(),
                                            snapshot.child("password").value.toString(),
                                            snapshot.child("firstName").value.toString(),
                                            snapshot.child("lastName").value.toString(),
                                            snapshot.child("phoneNumber").value.toString(),
                                            snapshot.child("imageUrl").value.toString(),
                                            ArrayList(),
                                            snapshot.child("points").value.toString().toIntOrNull()
                                        )
                                        sharedViewModel.user.points =
                                            sharedViewModel.user.points?.plus(10)
                                        DataBase.databaseUsers.child(
                                            sharedViewModel.name.replace(".", "").replace("#", "")
                                                .replace("$", "").replace("[", "").replace("]", "")
                                        ).setValue(sharedViewModel.user).addOnSuccessListener {


                                            Toast.makeText(
                                                context,
                                                "You earned 10 points",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }.addOnFailureListener {
                                            Toast.makeText(context, "Error", Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    }

                                }.addOnFailureListener {
                                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                                }

                            } else {
                                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                            }


                        }

                } else {

                    Toast.makeText(
                        context,
                        "All fields required, comment is neccessary for grades less than 6. Grades must be in range 1 - 10",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }
        cancel.setOnClickListener{
            findNavController().navigate(R.id.action_EditFragment_to_HomeFragment)
        }
        var openCameraButton:Button=view.findViewById(R.id.buttonCamera)
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
        var openGalleryButton:Button=view.findViewById(R.id.buttonGallery)

        openGalleryButton.setOnClickListener{
            if (checkGalleryPermission()) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                if(galleryIntent.resolveActivity(requireActivity().packageManager)!=null) {
                    startActivityForResult(galleryIntent, GALLERY_PERMISSION_REQUEST_CODE)
                }
            } else {
                // Ako dozvola nije odobrena, zahtevajte je
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    GALLERY_PERMISSION_REQUEST_CODE
                )
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
            picture.setImageBitmap(imageBitmap)
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
                picture.setImageBitmap(imageBitmap)

                // Otpremanje slike na Firebase Storage
                sendPicutreToFirestorageDownloadURLSendToRealtimeDatabase(imageBitmap)

            }
        }
    }

    private fun sendPicutreToFirestorageDownloadURLSendToRealtimeDatabase(imageBitmap:Bitmap)
    {
        val imagesRef = DataBase.storageRef.child("images/${System.currentTimeMillis()}.jpg")
        picture.visibility=View.GONE
        pictureWait.visibility=View.VISIBLE
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
                    picture.visibility=View.GONE
                    picture.visibility=View.VISIBLE
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