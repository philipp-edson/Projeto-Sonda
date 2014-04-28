package br.sci.appsondavalidation.model.dataloader.exception;

/**
 * 
 * @version 1.0.0
 * @author Rafael Carvalho Chagas
 * @since Copyright 2012-2013
 *
 */

public class LoaderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public LoaderException() {
		super();
	}
	
	public LoaderException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LoaderException(String message) {
		super(message);
	}
	
	public LoaderException(Throwable cause) {
		super(cause);
	}
}