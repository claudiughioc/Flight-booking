package airService.client;

import java.io.Serializable;

public class ArrayOfString implements Serializable{
	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	protected String []string;

	public ArrayOfString() {
	}

	public ArrayOfString(java.lang.String[] string) {
	    this.string = string;
	}

	public java.lang.String[] getString() {
	    return string;
	}

	public void setString(java.lang.String[] string) {
	    this.string = string;
	}
}
