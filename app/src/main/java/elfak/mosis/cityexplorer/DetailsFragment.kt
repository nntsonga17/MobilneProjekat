package elfak.mosis.cityexplorer

import android.content.ContentValues
import android.content.Intent
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import elfak.mosis.cityexplorer.data.UserData
import elfak.mosis.cityexplorer.model.LocationViewModel
import elfak.mosis.cityexplorer.model.UserViewModel
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import elfak.mosis.cityexplorer.data.MyPlaces
import java.io.ByteArrayOutputStream


class DetailsFragment : Fragment() {
    private  val sharedViewModel: UserViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    /////////////////////////////////////////////////////////////////////////////////
    private  var imgUrl:String=""
    private val REQUEST_IMAGE_CAPTURE = 1
    val GALLERY_PERMISSION_REQUEST_CODE = 1002
    val CAMERA_PERMISSION_REQUEST_CODE = 1001
    /////////////////////////////////////////////////////////////////////////////
    private lateinit var picture: ImageView
    private lateinit var name: TextView
    private lateinit var description: EditText
    private lateinit var grade:EditText
    private lateinit var update: Button
    private lateinit var load: ProgressBar
    private lateinit var back:Button
    private lateinit var delete:Button
    private lateinit var latitude:EditText
    private lateinit var longitude:EditText
    private lateinit var oldLatitude:TextView
    private lateinit var oldLongitude:TextView
    private lateinit var ownComments:Button
    private lateinit var bar:ProgressBar
    lateinit var type: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        picture=view.findViewById(R.id.objectPicture)
        type=view.findViewById(R.id.editTextTypeU)
        name=view.findViewById(R.id.editTextPlaceNameU)
        description=view.findViewById(R.id.editTextCommentU)
        grade=view.findViewById(R.id.editTextGradeU)
        load=view.findViewById(R.id.progressPlaceU)
        update=view.findViewById(R.id.buttonAddPlaceU)
        latitude=view.findViewById(R.id.Latitude)
        longitude=view.findViewById(R.id.Longitude)
        var openCameraButton:Button=view.findViewById(R.id.buttonAddUsingCameraU)
        var openGalleryButton:Button=view.findViewById(R.id.buttonAddUsingGaleryU)
        oldLatitude=view.findViewById(R.id.oldLatitude)
        oldLongitude=view.findViewById(R.id.oldLongitude)
        ownComments=view.findViewById(R.id.commentMyPlace)
        bar=view.findViewById(R.id.picBar)

        try {
            load.visibility=View.VISIBLE
            DataBase.databasePlaces.child(sharedViewModel.pickedPlace).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {

                    name.text = snapshot.child("name").value.toString()
                    description.setText( snapshot.child("comment").value.toString())
                    grade.setText(snapshot.child("grade").value.toString())
                    oldLatitude.text=snapshot.child("latitude").value.toString()
                    oldLongitude.text=snapshot.child("longitude").value.toString()
                    sharedViewModel.latitude=oldLatitude.text.toString()
                    sharedViewModel.longitude=oldLongitude.text.toString()
                    imgUrl = snapshot.child("img").value.toString()
                    downloadPicture()
                    load.visibility=View.GONE
                }
            }.addOnFailureListener { exception ->
                // Handle the exception here
                Log.e(ContentValues.TAG, "Error getting data from Firebase: ${exception.message}")
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error accessing Firebase: ${e.message}")
        }
        ownComments.setOnClickListener{
            locationViewModel.setName(sharedViewModel.pickedPlace)
            findNavController().navigate(R.id.action_DetailsFragment_to_CommentPlaceFragment)
        }
        back=view.findViewById(R.id.buttonBackU)
        back.setOnClickListener{
            findNavController().navigate(R.id.action_DetailsFragment_to_MyPlacesFragment)

        }

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
        var set:Button=view.findViewById(R.id.buttonSetU)
            set.setOnClickListener{
                sharedViewModel.latitude=oldLatitude.text.toString()
                sharedViewModel.longitude=oldLongitude.text.toString()
                locationViewModel.viewObject=false
                locationViewModel.addObject=false
                locationViewModel.oneObject=true

                findNavController().navigate(R.id.action_DetailsFragment_to_MapFragment)
            }
        var commentArray = ArrayList<Comments>()
        val nizObserver=Observer<ArrayList<Comments>>{newValue->
            commentArray=newValue


        }
        sharedViewModel.comments.observe(viewLifecycleOwner,nizObserver)
        ///////////////////////////////////////////////////////////////////////////////
        update.setOnClickListener{
            var Description=description.text.toString()
            val Grade=grade.text.toString()
            if((grade.text.toString().isNotEmpty()&&grade.text.toString().toInt()>=5&&grade.text.toString().toInt()<=10)||(grade.text.toString().isNotEmpty()&&grade.text.toString().toInt()<5&&grade.text.toString().toInt()>=1&&description.text.toString().isNotEmpty())) {

                load.visibility = View.VISIBLE
                val key = name.text.toString().replace(".", "").replace("#", "").replace("$", "")
                    .replace("[", "").replace("]", "")
                if(Description.isEmpty())
                {
                    Description=""
                }
                var lati=""
                var longi=""
                if(latitude.text.toString().isEmpty())
                {
                    lati=oldLatitude.text.toString()

                }
                if(longitude.text.toString().isEmpty())
                {
                    longi=oldLongitude.text.toString()

                }
                if(latitude.text.toString().isNotEmpty())
                {
                    lati=latitude.text.toString()
                }
                if(longitude.text.toString().isNotEmpty())
                {
                    longi=longitude.text.toString()
                }
                val placeP = MyPlaces(name.text.toString(),description.text.toString(),grade.text.toString(),sharedViewModel.name,longi,lati,imgUrl)

                DataBase.databasePlaces.child(key).setValue(placeP).addOnSuccessListener {
                    sharedViewModel.lastLatitude=placeP.latitude.toString()
                    sharedViewModel.lastLongitude=placeP.longitude.toString()

                    load.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Place successfully updated ${placeP.name}",
                        Toast.LENGTH_SHORT
                    ).show()

                    DataBase.databaseUsers.child(sharedViewModel.name.replace(".", "").replace("#", "").replace("$", "")
                        .replace("[", "").replace("]", "")).get().addOnSuccessListener { snapshot->
                        if(snapshot.exists())
                        {
                            sharedViewModel.user= UserData(snapshot.child("username").value.toString(),snapshot.child("password").value.toString(),snapshot.child("firstName").value.toString(),snapshot.child("lastName").value.toString(),snapshot.child("phoneNumber").value.toString(),snapshot.child("imageUrl").value.toString(),ArrayList(),snapshot.child("points").value.toString().toInt())
                            if(sharedViewModel.user.points!=null)
                            {
                                sharedViewModel.user.points=sharedViewModel.user.points?.plus(2)

                            }
                            DataBase.databaseUsers.child(sharedViewModel.name.replace(".", "").replace("#", "")
                                .replace("$", "").replace("[", "").replace("]", "")).setValue(sharedViewModel.user).addOnSuccessListener {
                                Toast.makeText(context,"You earned 2 more points",Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }.addOnFailureListener {
                                Toast.makeText(context,"Error",Toast.LENGTH_LONG).show()
                            }
                        }

                    }.addOnFailureListener {
                        Toast.makeText(context,"Error",Toast.LENGTH_LONG).show()
                    }
                    //findNavController().navigate(R.id.action_detaljniFragment2_to_mojaMestaFragment)


                }.addOnFailureListener {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT)
                }
            }
            else
            {
                Toast.makeText(context,"Fill all fields. Grades 1-5 need explanation", Toast.LENGTH_SHORT).show()
            }

        }
        delete=view.findViewById(R.id.buttonDeleteU)
        delete.setOnClickListener{
            DataBase.databasePlaces.child(name.text.toString()).removeValue().addOnSuccessListener {
                Toast.makeText(context,"Place sucessfully deleted ${name.text.toString()}",Toast.LENGTH_LONG).show()
                update.visibility=View.GONE
                delete.visibility=View.GONE
                set.visibility=View.GONE
                openCameraButton.visibility=View.GONE
                openGalleryButton.visibility=View.GONE
                ownComments.visibility=View.GONE
                sharedViewModel.latitude=""
                sharedViewModel.longitude=""
                for(comment1 in commentArray)
                {
                    Toast.makeText(context,"${comment1.place}",Toast.LENGTH_SHORT).show()
                    if(comment1.place==name.text.toString())
                    {
                        DataBase.databaseComments.child(comment1.id.toString()).removeValue().addOnSuccessListener {

                        }
                            .addOnFailureListener { exception->Toast.makeText(context,exception.toString(),Toast.LENGTH_LONG).show() }
                    }
                }



                DataBase.databaseUsers.child(sharedViewModel.name.replace(".", "").replace("#", "").replace("$", "")
                    .replace("[", "").replace("]", "")).get().addOnSuccessListener { snapshot->
                    if(snapshot.exists())
                    {
                        sharedViewModel.user=UserData(snapshot.child("username").value.toString(),snapshot.child("password").value.toString(),snapshot.child("firstName").value.toString(),snapshot.child("lastName").value.toString(),snapshot.child("phoneNumber").value.toString(),snapshot.child("imageUrl").value.toString(),ArrayList(),snapshot.child("points").value.toString().toInt())
                        if(sharedViewModel.user.points!=null)
                        {
                            sharedViewModel.user.points=sharedViewModel.user.points?.minus(10)

                        }
                        DataBase.databaseUsers.child(sharedViewModel.name.replace(".", "").replace("#", "")
                            .replace("$", "").replace("[", "").replace("]", "")).setValue(sharedViewModel.user).addOnSuccessListener {
                            Toast.makeText(context,"You lost 10 points",Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context,"Error",Toast.LENGTH_LONG).show()
                        }
                    }

                }.addOnFailureListener {
                    Toast.makeText(context,"Error",Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener{
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
            }



        }

            ///////////////////////////////////////////////////////////////////////////////

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




        return view
    }
    //FUNKCIJE ZA DOZVOLE
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
    //FUNKCIJA KOJA ODREDJUJE STA CE DA SE RADI KADA SE VRATIMO U ACTIVIY APLIKACIJE
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            //ZA SETOVANJE IMAGE VIEW-A
            picture.setImageBitmap(imageBitmap)
            sendPicToFirestorageDownloadURLSendToRealTimeDatabase(imageBitmap)
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
                sendPicToFirestorageDownloadURLSendToRealTimeDatabase(imageBitmap)

            }
        }
    }
    //FUNKCIJA ZA UPIS U BAZU PA PREUZIMANJE URL SLIKE SA STORIGA I CUVANJE U LOKALNU PROMENLJIVU KOJA CE SLUZITI ZA UZIMANJE PODATKA
//O ATRIBUTU Places.Img:String
    private fun downloadPicture() {
        bar.visibility=View.VISIBLE
        picture.visibility=View.GONE

        if (imgUrl != "") {
            Glide.with(requireContext())
                .load(imgUrl)
                .into(picture)
            bar.visibility=View.GONE
            picture.visibility=View.VISIBLE


        }
        else
        {
            bar.visibility=View.GONE
        }
    }
    private fun sendPicToFirestorageDownloadURLSendToRealTimeDatabase(imageBitmap:Bitmap)
    {
        val imagesRef = DataBase.storageRef.child("images/${System.currentTimeMillis()}.jpg")

        // Convert the bitmap to bytes
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        // Upload the image to Firebase Storage
        val uploadTask = imagesRef.putBytes(imageData)
        bar.visibility=View.VISIBLE
        picture.visibility=View.GONE
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image upload success
                // Now you can get the download URL of the image and save it to the database
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    // Save the URI to the database or use it as needed
                    imgUrl = uri.toString()
                    bar.visibility=View.GONE
                    picture.visibility=View.VISIBLE

                    downloadPicture()
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