package com.skirrs;

public class SkirrsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorCode;
	
	public SkirrsException ( String reason, int errorCode )
	{
		super( reason );
		this.errorCode = errorCode;
	}
	
	public SkirrsException ( String reason, int errorCode, Throwable cause )
	{
		super( reason, cause );
		this.errorCode = errorCode;
	}
	
	public int getErrorCode()
	{
		return errorCode;
	}
	
	public SkirrsException( int errorCode )
	{
		this.errorCode = errorCode;
	}

}
