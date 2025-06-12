package com.example.caloriecounter.utils;

import android.app.Activity;
import android.content.Intent;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

public class NavigationHelper {

    /**
     * Redirects the user to a specified activity while clearing the task stack.
     *
     * @param currentActivity The current activity from which navigation is being triggered.
     * @param destination The target activity class to which the user will be redirected.
     */
    public static void redirectTo(Activity currentActivity, Class<?> destination) {
        Intent intent = new Intent(currentActivity, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        currentActivity.startActivity(intent);
    }

    /**
     * Opens the navigation drawer from the left side of the screen.
     * This method checks if the drawer is not already open before attempting to open it.
     * It helps to avoid redundant actions when the drawer is already open.
     *
     * @param drawerLayout The DrawerLayout component to control the drawer's state.
     */
    public static void openDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**
     * Closes the navigation drawer if it is open.
     * This method ensures the drawer is only closed when it is open, preventing unnecessary actions.
     *
     * @param drawerLayout The DrawerLayout component to control the drawer's state.
     */
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
