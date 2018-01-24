package com.example.esatgozcu.haritalarkullanimi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button button;
    ListView listView;
    // Diğer sayfadan ulaşılabilir olması için static yapıyoruz.
    static ArrayList<String> names = new ArrayList<String>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=(Button)findViewById(R.id.addButton);
        listView=(ListView)findViewById(R.id.listView);

        getData();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(arrayAdapter);

        // Listview'deki itemlere tıklandığı zaman..
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Haritalar sayfasına geçiş yapıyoruz.
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                // Mevcut konumun görüntüleneceğini bildiriyoruz.
                intent.putExtra("info", "old");
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    // Veritabanından verileri arraylistlere aktarıyoruz.
    private void getData() {
        try {

            // Veritabanını açma veya yok ise oluşturma
            database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            // Tablo oluşturma
            Cursor cursor = database.rawQuery("SELECT * FROM places", null);

            // Verileri çekebilmek için index oluşturuyoruz
            int nameIx = cursor.getColumnIndex("name");
            int latitudeIx = cursor.getColumnIndex("latitude");
            int longitudeIx = cursor.getColumnIndex("longitude");

            // Veritabanın ilk satırına gidiyoruz
            cursor.moveToFirst();

            // İlk satırdan itibaren verileri teker teker çekiyoruz ve dizilere aktarıyoruz.
            while (cursor != null) {

                String nameFromDatabase = cursor.getString(nameIx);
                String latitudeFromDatabase = cursor.getString(latitudeIx);
                String longitudeFromDatabase = cursor.getString(longitudeIx);

                names.add(nameFromDatabase);

                Double l1 = Double.parseDouble(latitudeFromDatabase);
                Double l2 = Double.parseDouble(longitudeFromDatabase);

                LatLng locationFromDatabase = new LatLng(l1,l2);

                locations.add(locationFromDatabase);

                // Bir sonraki satıra geçiyoruz
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Ekle tuşuna basıldığı zaman..
    public void add (View view)
    {
        // Harita sayfasına geçiş yapıyoruz.
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        // Yeni konum ekleneceğini belirtiyoruz.
        intent.putExtra("info", "new");
        startActivity(intent);
    }
}
