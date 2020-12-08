package intro

class Movie(val name: String, val year: Int) {
  override def toString: String = "[" + name + ", " + year + "]"
}

object Movie {

  def getMovie(year: Int) = {
    year match {
      case 1999 => Some(new Movie("intro.Movie 1", 1999))
      case 2005 => Some(new Movie("intro.Movie 2", 2005))
      case _ => None
    }
  }
}