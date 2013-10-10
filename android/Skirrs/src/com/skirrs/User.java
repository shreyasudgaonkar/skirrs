package com.skirrs;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String  mUserId;
	public String  mFirstName;
	public String  mLastName;
	public String  mPhoneNumber;
	public boolean mFbReg;
	public String  mEmail;
	
	public User()
	{
		
	}
	
	public User( String userId,
				 String firstName,
				 String lastName,
				 String email )
	{
		
		mUserId    = userId;
		mFirstName = firstName;
		mLastName  = lastName;
		mEmail     = email;
		
		mFbReg = false;
	}
	
	
}
