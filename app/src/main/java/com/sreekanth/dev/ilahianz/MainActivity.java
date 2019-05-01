package com.sreekanth.dev.ilahianz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sreekanth.dev.ilahianz.Supports.Supports;
import com.sreekanth.dev.ilahianz.Supports.ViewSupport;
import com.sreekanth.dev.ilahianz.model.User;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String ATTENDANCE_WEBSITE = "http://ilahiacollege.info/StudentPanel/studAttendance.aspx";
    private final String ILAHIA_WEBSITE = "http://ilahiaartscollege.org/";
    private final LatLng ILAHIA_LOCATION = new LatLng(10.025716, 76.567840);

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    LinearLayout chat, Notes, remainder, teachers, notifications, help, location, attendance, website;
    Dialog Notificatio_popup, profile;
    DatabaseReference reference;
    RelativeLayout profile_view;
    RelativeLayout connectionStatus;
    ProgressBar progressbar;
    CircleImageView Header_DP;
    TextView header_username,
            initial, email, retry;
    User myInfo = new User();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        profile = new Dialog(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Notes = findViewById(R.id.Notes);
        remainder = findViewById(R.id.remainder);
        notifications = findViewById(R.id.notification);
        teachers = findViewById(R.id.teachers);
        help = findViewById(R.id.help);
        chat = findViewById(R.id.chat);
        location = findViewById(R.id.location);
        attendance = findViewById(R.id.attendance);
        website = findViewById(R.id.website);
        profile_view = header.findViewById(R.id.profile);
        connectionStatus = header.findViewById(R.id.retry_btn);
        progressbar = header.findViewById(R.id.progressBar);
        Header_DP = header.findViewById(R.id.imageView);
        header_username = header.findViewById(R.id.username);
        initial = header.findViewById(R.id.initial);
        email = header.findViewById(R.id.email);
        retry = header.findViewById(R.id.retry_button);

        init();

        Header_DP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                        new Pair<View, String>(Header_DP, "imageTransition"));
                startActivity(new Intent(MainActivity.this, ProfileActivity.class), options.toBundle());
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionStatus.setVisibility(View.GONE);
                init();
            }
        });
        ////////////////////////////////////////////////////
        teachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TeachersList.class);
                startActivity(intent);
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        remainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RemainderActivity.class);
                startActivity(intent);
            }
        });
        Notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                startActivity(intent);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });
        attendance.setOnClickListener(new View.OnClickListener() { //Visit the Ilahia Website
            @Override
            public void onClick(View v) {
                WebViewActivity.setURL(ATTENDANCE_WEBSITE);
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.setURL(ILAHIA_WEBSITE);
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.setLOCATION(ILAHIA_LOCATION, "Ilahia College of Arts & Science");
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        header_username.setText(getUserInfo("username"));
        initial.setText(getUserInfo("className"));
        if (!TextUtils.equals(getUserInfo("nickName"), "default")) {
            header_username.setText(String.format("%s (%s)", getUserInfo("username").toUpperCase(),
                    getUserInfo("nickName")));
        } else {
            header_username.setText(getUserInfo("username").toUpperCase());
        }
        email.setText(getUserInfo("email"));

        if (Supports.Connected(this)) {
            drawer.openDrawer(GravityCompat.START);
            profile_view.setVisibility(View.GONE);
            connectionStatus.setVisibility(View.VISIBLE);
        } else {
            profile_view.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.VISIBLE);
            connectionStatus.setVisibility(View.GONE);
            reference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(Objects.requireNonNull(firebaseAuth.getUid()));
            reference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    progressbar.setVisibility(View.GONE);
                    assert user != null;
                    setUserInfo(user);
                    myInfo = user;
                    ViewSupport.setThumbProfileImage(user, Header_DP);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Could not Connect with Database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getUserInfo(String key) {
        SharedPreferences preferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        return preferences.getString(key, "none");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class)
                    .putExtra("ProfileURL", myInfo.getThumbnailURL()));
        } else if (id == R.id.action_help) {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
        } else if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.action_Logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, signinActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, signinActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

        } else if (id == R.id.nav_invite) {

        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setUserInfo(User user) {
        setUserInfo("username", user.getUsername());
        setUserInfo("number", user.getPhoneNumber());
        setUserInfo("gender", user.getGender());
        setUserInfo("className", user.getClassName());
        setUserInfo("email", user.getEmail());
        setUserInfo("Birthday", user.getBirthday());
        setUserInfo("BirthYear", user.getBirthYear());
        setUserInfo("BirthMonth", user.getBirthMonth());
        setUserInfo("nickname", user.getNickname());
        setUserInfo("category", user.getCategory());
        setUserInfo("description", user.getDescription());
        setUserInfo("LastSeenPrivacy", user.getLastSeenPrivacy());
        setUserInfo("ProfilePrivacy", user.getProfilePrivacy());
        setUserInfo("AboutPrivacy", user.getAboutPrivacy());
        setUserInfo("LocationPrivacy", user.getLocationPrivacy());
        setUserInfo("EmailPrivacy", user.getEmailPrivacy());
        setUserInfo("PhonePrivacy", user.getPhonePrivacy());
        setUserInfo("BirthdayPrivacy", user.getBirthdayPrivacy());
    }

    private void setUserInfo(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void NotificationPopup(Context context, String title, String content, String time, String date, String to, String from) {
        Notificatio_popup = new Dialog(context);
        Notificatio_popup.setContentView(R.layout.popup_notification);
        TextView title_txt = Notificatio_popup.findViewById(R.id.title_notify);
        TextView content_txt = Notificatio_popup.findViewById(R.id.content_notify);
        TextView dateInfo = Notificatio_popup.findViewById(R.id.date_notify);
        TextView fromInfo = Notificatio_popup.findViewById(R.id.from_notify);
        TextView toInfo = Notificatio_popup.findViewById(R.id.public_or_private);
        TextView timeInfo = Notificatio_popup.findViewById(R.id.time_notify);
        Button rembr_btn = Notificatio_popup.findViewById(R.id.remember_btn_notify);
        Button ok_btn = Notificatio_popup.findViewById(R.id.ok_btn_notify);
        ImageView status = Notificatio_popup.findViewById(R.id.status_notify);
        title_txt.setText(title);
        content_txt.setText(content);
        dateInfo.setText(date);
        fromInfo.setText(from);
        toInfo.setText(to);
        timeInfo.setText(time);
        if (to.equals("public")) {
            status.setImageResource(R.drawable.ic_public_public_24dp);
        } else {
            status.setImageResource(R.drawable.ic_supervisor_account_black_24dp);
        }
        Objects.requireNonNull(Notificatio_popup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notificatio_popup.cancel();
            }
        });
        rembr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notificatio_popup.cancel();
            }
        });
        Notificatio_popup.show();

    }
}