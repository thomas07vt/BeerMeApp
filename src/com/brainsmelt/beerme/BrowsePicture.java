package com.brainsmelt.beerme;


//public class BrowsePicture extends Activity {
//
//    private static final int SELECT_PICTURE = 1;
//
//    private String selectedImagePath;
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//        ((Button) findViewById(R.id.Button01))
//                .setOnClickListener(new OnClickListener() {
//
//                    public void onClick(View arg0) {
//
//                        // in onCreate or any event where your want the user to
//                        // select a file
//                        Intent intent = new Intent();
//                        intent.setType("image/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        startActivityForResult(Intent.createChooser(intent,
//                                "Select Picture"), SELECT_PICTURE);
//                    }
//                });
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                Uri selectedImageUri = data.getData();
//                selectedImagePath = getPath(selectedImageUri);
//            }
//        }
//    }
//
//    public String getPath(Uri uri) {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor
//                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
//
//}
