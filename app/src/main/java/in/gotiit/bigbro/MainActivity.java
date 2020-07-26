package in.gotiit.bigbro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import in.gotiit.bigbro.db.QuestionWithUser;
import in.gotiit.bigbro.util.App;
import in.gotiit.bigbro.util.Util;

import static in.gotiit.bigbro.ChatsFragment.QUESTION;
import static in.gotiit.bigbro.QuestionsFragment.SOURCE;
import static in.gotiit.bigbro.QuestionsFragment.SOURCE_HOME;

public class MainActivity extends AppCompatActivity {

    public NavController navController;
    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        TextView tvFullName = navHeader.findViewById(R.id.drawer_full_name);
        TextView tvUsername = navHeader.findViewById(R.id.drawer_username);
        tvFullName.setText(App.instance().getUser().fullName);
        tvUsername.setText(Util.atWrapUsername(App.instance().getUser().userName));
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.QuestionsFragment
        ).setDrawerLayout(drawer).build();
    }

    @Override
    protected void onStart() {
        setSupportActionBar(toolbar);
        super.onStart();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(item -> {
            Bundle args;
            switch(item.getItemId()) {
                case R.id.nav_home:
                    args = new Bundle();
                    args.putInt(SOURCE, QuestionsFragment.SOURCE_HOME);
                    navController.navigate(R.id.action_QuestionsFragment_self, args);
                    break;
                case R.id.nav_feed:
                    args = new Bundle();
                    args.putInt(SOURCE, QuestionsFragment.SOURCE_FEED);
                    navController.navigate(R.id.action_QuestionsFragment_self, args);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            if (args == null) return;
            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) return;
            switch (destination.getId()) {
                case R.id.QuestionsFragment:
                    int source = args.getInt(SOURCE);
                    actionBar.setTitle(source == SOURCE_HOME ? "Home" : "Feed");
                    break;
                case R.id.ChatFragment:
                    QuestionWithUser question = (QuestionWithUser) args.getSerializable(QUESTION);
                    if (question == null) return;
                    actionBar.setTitle(question.getTitle());
                    break;
            }
        });
        App.instance().getWebSocket();//starts the WebSocket
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void switchFragment(int destId){
        navController.navigate(destId);
    }

}
