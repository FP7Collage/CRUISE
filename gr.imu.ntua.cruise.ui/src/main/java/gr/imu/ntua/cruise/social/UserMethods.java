/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.cruise.social;

import java.util.ArrayList;

/**
 *
 * @author imu-user
 */
public class UserMethods {
    
    
    private static final ArrayList<String> currentUser = new ArrayList<String>();

	public static ArrayList<String> getCurrentUser() {
		ArrayList<String> user =  new ArrayList<String>(currentUser);
                
		if (user == null) {
			throw new IllegalStateException("No user is currently signed in");
		}
		return user;
	}

	public static void setCurrentUser(ArrayList<String> user) {
		currentUser.add(user.get(0));
                currentUser.add(user.get(1));
	}
    
    
}
