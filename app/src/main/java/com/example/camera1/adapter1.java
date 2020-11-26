package com.example.camera1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.camera1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class adapter1 extends RecyclerView.Adapter<adapter1.ViewHolder>
{

    private Context c;
    private LayoutInflater layoutInflater;
    //private List<String> data;
    private List<String>data1;
    //private List<String>images;
    adapter1(Context context,List<String > data1)
    {
        this.c=context;
        this.layoutInflater=LayoutInflater.from(context);
        //this.data=data;
        this.data1=data1;
        // this.images=images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.custom_view,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String subtile=data1.get(position);
        holder.x1.setText(subtile);

//        URL newurl = new URL("https://firebasestorage.googleapis.com/v0/b/database-7cf0e.appspot.com/o/images%2Frivers3.jpg?alt=media&token=c0faec49-32d3-4b55-9886-e0dada342f23");
//        Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
        //holder.i.setImageBitmap(b);

        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(holder.i);

//        try {
//            //ImageView i = (ImageView)findViewById(R.id.image);
//            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL("https://firebasestorage.googleapis.com/v0/b/database-7cf0e.appspot.com/o/images%2Frivers3.jpg?alt=media&token=c0faec49-32d3-4b55-9886-e0dada342f23").getContent());
//            holder.i.setImageBitmap(bitmap);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //Picasso.with(getContext()).load(imgUrl).fit().into(contentImageView);

        holder.chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                // String text=c1.getText().toString();
                if (isChecked)
                {

                    Toast.makeText(c, "C1 is checked", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "ischeck: "+text);
                    mDatabase.child("users").child(data1.get(position)).child("status").setValue("true");
                }
                else
                {
                    mDatabase.child("users").child(data1.get(position)).child("status").setValue("false");
                }
            }
        });
//        holder.chkSelected.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
//        holder.chkSelected.setChecked(objIncome.isSelected());
//
//        holder.chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                DatabaseReference mDatabase;
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//
////                mDatabase.child("location_info").child("1").child("status").getValue("false");
//                mDatabase.child("location_info").child("2").child("status").setValue("false");
//                //set your object's last status
//               // objIncome.setSelected(isChecked);
//            }
//        });



//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Toast.makeText(this, "Recycle Click" + position, Toast.LENGTH_SHORT).show();
//                Log.d("sjdka","jsdj"+position);
//
//                AlertDialog alertDialog = new AlertDialog.Builder(c)
////set icon
//                        .setIcon(android.R.drawable.ic_dialog_alert)
////set title
//                        .setTitle("Are you sure to Exit")
////set message
//                        .setMessage("Exiting will call finish() method")
////set positive button
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                DatabaseReference mDatabase;
//                                mDatabase = FirebaseDatabase.getInstance().getReference();
//                               // String x=data1[position]
//                                mDatabase.child("location_info").child(data1.get(position)).child("status").setValue("false");
//                                //set what would happen when positive button is clicked
//                                //finish();
//                            }
//                        })
////set negative button
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //set what should happen when negative button is clicked
//                                Toast.makeText(c,"Nothing Happened",Toast.LENGTH_LONG).show();
//                            }
//                        })
//                        .show();
//
//               // mDatabase.child("location_info").child("1").child("status").getValue("false");
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data1.size();
    }
    //    public static Bitmap loadBitmap(String url) {
//        Bitmap bitmap = null;
//        InputStream in = null;
//        BufferedOutputStream out = null;
//
//        try {
//            in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);
//
//            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
//            copy(in, out);
//            out.flush();
//
//            final byte[] data = dataStream.toByteArray();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            //options.inSampleSize = 1;
//
//            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
//        } catch (IOException e) {
//            Log.e(TAG, "Could not load Bitmap from: " + url);
//        } finally {
//            closeStream(in);
//            closeStream(out);
//        }
//
//        return bitmap;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView x1;
        //ImageView i;
        public CheckBox chkSelected;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            //x=itemView.findViewById(R.id.textView);
            x1=itemView.findViewById(R.id.textView2);
            chkSelected=itemView.findViewById(R.id.checkBox2);
            // i=itemView.findViewById(R.id.imageView);
        }
    }

}
