import java.io.{File, FileNotFoundException, FileOutputStream, IOException}
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.itextpdf.text.{Chunk, Document, DocumentException, Element, Font, FontFactory, Image, Paragraph}
import com.itextpdf.text.pdf.{PdfPCell, PdfPTable, PdfWriter}
import com.itextpdf.text.BaseColor
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.ListSet
//import scalaInvoice.FileName
import java.io.File
import better.files._
import better.files.File._

class WriteToPDF {
  private val FILE_NAME = "E:\\invoice_Report\\"
  val getFileName:String => String = str => str.split(",")(0)//.replace(" ", "")

  def getDirWithMMMyyyy(pdfNames: ListBuffer[ListBuffer[String]]):ListBuffer[String]= {
    var MMMyyyy: ListSet[String] = new ListSet[String]
    for (i <- 0 until pdfNames.size) {
      for (j <- 0 until pdfNames(i).size) {
        val details = getStrudentEmiDetails(pdfNames(i)(j))

        val dateInMMMYYYY = s"${details(3).split("-")(1)}-${details(3).split("-")(2)}" // ex. Aug-2018
        val itr = pdfNames.iterator
        while (itr.hasNext) { MMMyyyy += dateInMMMYYYY; itr.next()  }
      }
    }
    var dirInMMMyyyy:ListBuffer[String] = new ListBuffer[String]
    val dirs = MMMyyyy.toList
    for(i <- 0 until dirs.size){ dirInMMMyyyy += s"${dirs(i)}"}
    for(i <- 0 until dirInMMMyyyy.size){
      if(s"${FILE_NAME}monthWise\\${dirInMMMyyyy(i)}".toFile.exists)s"${FILE_NAME}monthWise\\${dirInMMMyyyy(i)}".toFile.delete(true)
      val dir:better.files.File = s"${FILE_NAME}monthWise\\${dirInMMMyyyy(i)}".toFile.createIfNotExists(true)
    }
    dirInMMMyyyy
  } //ListBuffer(Aug-2018, Sep-2018, Oct-2018, Dec-2018, Jan-2019)

  def getDirWithName(pdfNames: ListBuffer[ListBuffer[String]]):ListBuffer[String]= {
    var stdNames: ListSet[String] = new ListSet[String]
    var stdID: ListSet[String] = new ListSet[String]

    //ToGet StudentName and StrudentID
    for (i <- 0 until pdfNames.size) {
      for (j <- 0 until pdfNames(i).size) {
        val details = getStrudentEmiDetails(pdfNames(i)(j))

        val stdentName = details(0)
        val itr = pdfNames.iterator
        while (itr.hasNext) { stdNames += stdentName; itr.next  }

        val studentID = details(1)
        val itr1 = pdfNames.iterator
        while (itr1.hasNext){ stdID += studentID; itr1.next()}
      }
    }
    // join the StudentName and ID string and create directory with the Joined String
    var dirInNames:ListBuffer[String] = new ListBuffer[String]
    val dirs = stdNames.toList
    val studentID = stdID.toList
    for(i <- 0 until dirs.size){ dirInNames += s"${dirs(i)}_OLC${studentID(i)}"}   //(Karan Kumar_OLC00002)
    for(i <- 0 until dirInNames.size){
      if(s"${FILE_NAME}studentWise\\${dirInNames(i)}".toFile.exists)s"${FILE_NAME}studentWise\\${dirInNames(i)}".toFile.delete(true)
      val dir:better.files.File = s"${FILE_NAME}studentWise\\${dirInNames(i)}".toFile.createIfNotExists(true)
    }
    dirInNames
  }  //ListBuffer(Suraj Ghimire_OLC00001, Karan Kumar_OLC00002)

  def getStrudentEmiDetails(str: String):List[String]={
    val name_id_emiP_ddMMMYYYY_emiAmt: String =  str.split(",")(0)
    val details = name_id_emiP_ddMMMYYYY_emiAmt.split("_")
    val stdentName:String = details(0)
    val studentID:String = details(1)
    val emiNo = details(2).substring(details(2).length-1)
    val date = details(3)
    val dateInMMMYYYY = s"${details(3).split("-")(1)}-${details(3).split("-")(2)}"
    val emiAmt = details(4)
    val mobleNum:String = "+91 "+str.split(",")(1)
    val course_taken = str.split(",")(2)
    val invoiceID = s"Invoice# OLC-${studentID}-${emiNo}"
    val studentDetails:List[String] = List(stdentName, studentID, emiNo, date, dateInMMMYYYY, emiAmt, mobleNum, course_taken, invoiceID)
    studentDetails
  } //will return Lists example of one list: List(Suraj Ghimire, 00001, 1, 1-Aug-2018, Aug-2018, 5000, +91 903 241 2236, Core Java, Invoice# OLC-00001-1)

  val boldChunk:String => Chunk = (msg) => {val boldChunkObj = new Chunk(msg, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD)); boldChunkObj}
  val chunk:String => Chunk = (msg) => {val chunkObj = new Chunk(msg, FontFactory.getFont(FontFactory.TIMES_ROMAN, 10)); chunkObj}
  val newLine:Document=>Unit = document => document.add(new Paragraph(Chunk.NEWLINE))
  def verticalPositionMark():Chunk = {val vpmChunk = new Chunk(new Chunk(new VerticalPositionMark)); vpmChunk}
  val emiNoWtGrm:String => String = emiNo =>{
    emiNo match {
      case "1" => "1st"
      case "2" => "2nd"
      case "3" => "3rd"
      case "4" => "4th"
      case _   => "5th"
    }
  }

  def writeInvoiceDetailtoPDF(pdfNames: ListBuffer[ListBuffer[String]], directoryNames:ListBuffer[String], directoryMMMyyyy:ListBuffer[String]): Unit = {
    var stdNames: ListSet[String] = new ListSet[String]
    var stdID: ListSet[String] = new ListSet[String]
    try
        for (i <- 0 until pdfNames.size) {
          for (j <- 0 until pdfNames(i).size) {
            //pdfNames having extra details of mobile and course Taken to skip this call getFileName
            val fileName = getFileName(pdfNames(i)(j)) //println(fileName) 1 Exp: Suraj Ghimire_00001_part1_1-Aug-2018_5000
            val details = getStrudentEmiDetails(pdfNames(i)(j))
            val stdentName = details(0)
            val studentID = details(1)
            val emiNo = details(2)
            val date = details(3)
            val dateInMMMYYYY = details(4)
            val emiAmt = details(5) + ".0"
            val mobleNum = details(6)
            val course_taken = details(7)
            val invoiceID = details(8)
            //println(s"name: $stdentName  ID: $studentID emiNo: $emiNo  date: $date MMMYYY: $dateInMMMYYYY emiAmt: $emiAmt Mobile: $mobleNum course: $course_taken $invoiceID")
            //1Exp: name: Suraj Ghimire  ID: 00001 emiNo: 1  date: 1-Aug-2018 MMMYYY: Aug-2018 emiAmt: 5000.0 Mobile: +91 903 241 2236 course: Core Java Invoice# OLC-00001-1

            val document = new Document

            // Creating the Directory with MMM-yyyy
            //println(directoryMMMyyyy)

            for(i <- 0 until directoryMMMyyyy.size){

              var dMMMyyyy = directoryMMMyyyy(i) //println(dMMMyyyy) output of all emi paided date in 1Ex: Aug-2018
              if( dMMMyyyy == dateInMMMYYYY){
                //var filedirloc = s"${dateInMMMYYYY(i)}//${fileName}"
                PdfWriter.getInstance(document, new FileOutputStream(new File(s"${FILE_NAME}monthWise\\${dMMMyyyy}\\${fileName}.pdf")))
                // println(s"${FILE_NAME}monthWise\\${dMMMyyyy}\\${fileName}.pdf    $dateInMMMYYYY")

              }
            }

            // Creating the Directory with Name-ID
            for(i <- 0 until directoryNames.size){
              var dName = directoryNames(i).split("_")(0) //; println(dName) output lists of stdentName 1Exp: Suraj Ghimire
              if( dName == stdentName){
                var filedirloc = s"${directoryNames(i)}//${fileName}"
                PdfWriter.getInstance(document, new FileOutputStream(new File(s"${FILE_NAME}studentWise\\${filedirloc}.pdf")))
                //println(s"${FILE_NAME}${filedirloc}.pdf")
              }
            }
            //open
            document.open()
            //adding Image
            document.add(image("E:\\BigData\\OLC\\workspace\\scalaExamples\\dataset\\OLC_Logo.jpg"))

            //Custom Fond Style
            val boldFont15 = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD)
            val paragraph = new Paragraph("INVOICE", boldFont15)
            paragraph.setAlignment(Element.ALIGN_RIGHT)
            document.add(paragraph)

            newLine(document)

            val para1 = new Paragraph(chunk("3rd Floor, Plot#53"))
            para1.add(verticalPositionMark)
            para1.add(boldChunk(s"$date"))
            document.add(para1)

            val para2 = new Paragraph(chunk("Venkanna Hills, Chintal, Quthubullapur, Hyderabad, 500055"))
            para2.add(verticalPositionMark)
            para2.add(boldChunk(s"$invoiceID"))
            document.add(para2)

            document.add(chunk("GSTIN NO: "))
            document.add(boldChunk("36AACCO7284M1ZU"))

            newLine(document)

            val para3 = new Paragraph(chunk("Phone: +91 7 999 01 02 03"))
            para3.add(verticalPositionMark)
            para3.add(boldChunk(s"Invoice to: $stdentName"))
            document.add(para3)

            val para4 = new Paragraph(chunk("info@onlinelearningcenter.in"))
            para4.add(verticalPositionMark)
            para4.add(boldChunk(s"Student Contact Number: $mobleNum"))
            document.add(para4)

            newLine(document)
            newLine(document)

            document.add(chunk("Dear "))
            document.add(boldChunk(s"$stdentName"))
            document.add(chunk(","))

            newLine(document)

            val para5 = new Paragraph(chunk(s"Please find the receipt of your Invoice for the month of $dateInMMMYYYY, paid as ${emiNoWtGrm(emiNo)} installment of the below course."))
            document.add(para5)

            newLine(document)
            newLine(document)

            //Table
            val tableColumns = List("#", "CourseName", "Qty", "UnitPrice(INR)", "Total(INR)", "1", s"$course_taken with $course_taken-Installment $emiNo", "1", s"${emiAmt}", s"${emiAmt}")
            val table = pdfTable(tableColumns)
            document.add(table)

            newLine(document)
            newLine(document)

            val para6 = new Paragraph
            para6.setAlignment(Element.ALIGN_RIGHT)
            para6.add(chunk("Subtotal"))
            para6.add(Chunk.TABBING)
            para6.add(Chunk.SPACETABBING)
            para6.add(boldChunk(s"$emiAmt"))
            document.add(para6)

            val para7 = new Paragraph
            para7.setAlignment(Element.ALIGN_RIGHT)
            para7.add(chunk("GST (18%)"))
            para7.add(Chunk.TABBING)
            para7.add(boldChunk(s"${(emiAmt.toFloat * 0.18f).toString}"))
            document.add(para7)

            val para8 = new Paragraph
            para8.setAlignment(Element.ALIGN_RIGHT)
            para8.add(chunk("Total"))
            para8.add(Chunk.TABBING)
            para8.add(Chunk.SPACETABBING)
            para8.add(boldChunk(s"${(emiAmt.toFloat + emiAmt.toFloat * 0.18f).toString}"))
            document.add(para8)

            newLine(document)

            val para9 = new Paragraph
            para9.setAlignment(Element.ALIGN_RIGHT)
            para9.add(chunk("Looking Forward,"))
            document.add(para9)

            val para10 = new Paragraph
            para10.setAlignment(Element.ALIGN_RIGHT)
            para10.add(boldChunk("Online Learning Center Pvt Ltd."))
            document.add(para10)

            newLine(document)

            val para11 = new Paragraph
            para11.setAlignment(Element.ALIGN_CENTER)
            para11.add(new Chunk(boldChunk("This is a e-bill and does not need any signature")))
            document.add(para11)
            //close
            document.close()
          }
        }
    catch {
      case e@(_: FileNotFoundException | _: DocumentException) => e.printStackTrace()
      case e: IOException => e.printStackTrace()
    }
  }
  val cellAlignment:PdfPCell=>Unit = cell => { cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setVerticalAlignment(Element.ALIGN_CENTER)  }
  def cellColumn(column:String):PdfPCell ={ val cell = new PdfPCell(new Paragraph(column)); cell}
  def pdfTable(tableData:List[String]):PdfPTable ={
    val table = new PdfPTable(5) // 3 columns.
    table.setWidths(Array[Float](10, 40,10,20,20))
    for(i <- 0 until 5){
      var pdfPCell = new PdfPCell(new Paragraph(boldChunk(tableData(i))))
      cellAlignment(pdfPCell)
      pdfPCell.setBackgroundColor(BaseColor.GRAY)
      table.addCell(pdfPCell)
      table.setWidthPercentage(100)
    }
    for(i <- 5 until tableData.size){
      var pdfPCell = new PdfPCell(new Paragraph(boldChunk(tableData(i))))
      cellAlignment(pdfPCell)
      pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY)
      table.addCell(pdfPCell)
      table.setWidthPercentage(100)
    }
    table
  }
  def image(filename:String):Image = {
    val image = Image.getInstance(filename)
    image.scalePercent(50f)
    image.setAlignment(Image.LEFT)
    image
  }
}