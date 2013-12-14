package models.forms

/**
 * Stripped down Form Model to protect the "secret" fields like db unique key etc.
 * Will be expanded as soon as we have image urls etc...
 * @author Nemo
 */
case class FormCard(content: String, hat: String)