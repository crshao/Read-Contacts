package com.example.readcontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button loadContacts;
    private TextView listContacts;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listContacts = findViewById(R.id.listContacts);
        loadContacts = findViewById(R.id.loadContacts);
        getPermissionToReadContacts();

        listContacts.setMovementMethod(new ScrollingMovementMethod());

        loadContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadContacts();
            }
        });
    }

    private void getPermissionToReadContacts() {

        //Jika masuk ke if ini, artinya permission belum digranted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {
            //Mengecek bila pengguna sudah pernah dimintai permission dan menolaknya
            //if dibawah berfungsi menjelaskan kepada pengguna mengapa kita memerlukan permission ini
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
            {
                Toast.makeText(this, "PLEASE, JUST ONCE", Toast.LENGTH_SHORT).show();
            }

            //Lakukan request untuk meminta permission (menampilkan jendelanya)
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    //Callback yg membawa hasil dari pemanggilan requestPermission()


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //pengecekan ini akan memastikan bahwa hasil yg diberikan
        //berasal dari request yg kita lakukan berdasarkan kode diatas
        if(requestCode == READ_CONTACTS_PERMISSIONS_REQUEST)
        {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            else
            {
                //showRationale = false, jika pengguna memilih never ask again
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);

                if(showRationale)
                {
                    Toast.makeText(this, "Ini APA", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadContacts()
    {
        StringBuilder builder = new StringBuilder();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext())
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if(hasPhoneNumber > 0)
                {
                    Cursor cursor2 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[] {id},
                            null);

                    while(cursor2.moveToNext())
                    {
                        String phoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        builder.append("Contact: ").append(name).append(", Phone Number: ").append(phoneNumber).append("\n\n");
                    }
                    cursor2.close();
                }
            }
        }
        cursor.close();

        listContacts.setText(builder.toString());
    }

}
