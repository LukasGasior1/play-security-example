package object controllers {

  implicit class StringWithEncode(str: String) {
    def encode(): String = {
      val md = java.security.MessageDigest.getInstance("SHA-1")
      new String(md.digest(str.getBytes))
    }
  }
}
