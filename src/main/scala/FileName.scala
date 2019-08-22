/* Bala_00065_part1_9-February-2019_20000     UName_stuID_EMIno_<date>_RsPaid   */
import scala.collection.mutable.ListBuffer
import java.text.DateFormat
class FileName {
  var studentName:String = null
  var studentSNO:String = null
  var emi:Int = 0
  var emiDate:String = null
  var emiAmountPaid:Float = 0f
  var totalEMI:Int = 0
  var course_taken:String = null
  var mobileNo:String = null

  def fileNameDesign(stdInfo:ListBuffer[Student]):ListBuffer[ListBuffer[String]] ={
    val emiUpString:ListBuffer[ListBuffer[String]] = new ListBuffer[ListBuffer[String]]
    var emiString:ListBuffer[String] = null
    for(i <- 0 until stdInfo.size){
      studentSNO = studentID(stdInfo(i).sno)
      studentName = stdInfo(i).studentName
      totalEMI = stdInfo(i).payment.emi.size
      course_taken = stdInfo(i).course_taken
      mobileNo = stdInfo(i).studentPhone

      emiString = new ListBuffer[String]
      for ( t <- 0 until  totalEMI){ emiString.append( s"${studentName}_${studentSNO}_part${t+1}_${dateToString(stdInfo(i).payment.date(t))}_${stdInfo(i).payment.emi(t).toInt}" +
        s",${mobileNo},${course_taken}")}
      emiUpString.append(emiString)
    }
    emiUpString
  }

  def dateToString (date: java.util.Date):String = {
    val DateToStr: String = DateFormat.getDateInstance.format(date)
    DateToStr.replace(" ","-").replace(",", "")
  }

  def studentID(num: Int): String = {
    var strNum = num.toString
    val limitTo = 5
    if (num < 0) strNum = "0"
    else if (num.toString.length > limitTo)
      strNum = num.toString.substring(0, limitTo)
    "0" * (limitTo - strNum.length) + strNum
  }
}