import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import java.text.SimpleDateFormat
import java.util.Date

case class Student(sno:Int, studentName: String, studentEmail:String, studentPhone: String, studentAadhar: String, course_taken: String, totalFees: Float, payment:Payment)
case class Payment(emi: ListBuffer[Float], date: ListBuffer[Date])

class StudentInfo {

  val dateString: String => java.util.Date = str => {
    val sdf: SimpleDateFormat = new SimpleDateFormat("dd-MMM-yy")
    val parsedDate: java.util.Date = sdf.parse(str)
    parsedDate
  }

  def studentDetails(details:ListBuffer[ListBuffer[String]]): ListBuffer[Student] ={

    val stuDetails:ListBuffer[Student] = new ListBuffer[Student]

    val emiUpDetails:ListBuffer[ListBuffer[Float]] = new ListBuffer[ListBuffer[Float]]
    var emiDetails:ListBuffer[Float] = null

    val dateUpDetails:ListBuffer[ListBuffer[Date]] = new ListBuffer[ListBuffer[Date]]
    var dateDetails:ListBuffer[Date] = null

    for (i <- 0 until details.size){

      emiDetails = new ListBuffer[Float]
      for(j <- 7 until details(i).size by 2) emiDetails.append(details(i)(j).toFloat)
      emiUpDetails.append(emiDetails)

      dateDetails = new ListBuffer[Date]
      for (j <- 8 until details(i).size by (2)) dateDetails.append(dateString(details(i)(j)))
      dateUpDetails.append(dateDetails)

      val s:Student = Student(details(i)(0).toString.toFloat.toInt, details(i)(1), details(i)(2), details(i)(3), details(i)(4), details(i)(5), details(i)(6).toFloat, Payment(emiUpDetails(i), dateUpDetails(i)))
      stuDetails.append(s)
    }
    stuDetails
  }
}