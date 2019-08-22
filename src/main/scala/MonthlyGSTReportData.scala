import java.io.{File, FileNotFoundException, FileOutputStream, IOException}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.HashSet
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFFont

case class GstData(var sno:String, invoiceNo:String, invoiceDate:String, customerGSTNO:String, urGSTNo:String, totalInvoiceValue:String, taxableValue:String, cgst:String, sgst:String,igst:String, state:String, percentage:String)

class MonthlyGSTReportData {
 // private val FILE_NAME = "F:/MyFirstExcel.xlsx"
  def getFiles(path: String, fileList: ListBuffer[File]): Unit = {

    val directory: File = new File(path)
    var fList = directory.listFiles()
    if (fList != 0) for (file: File <- fList) {
      if (file.isFile) fileList.append(file)
      else if (file.isDirectory) {
        getFiles(file.getAbsolutePath, fileList)
      }
    }
  } // getthe filenames: ListBuffer(F:\invoice_Report\monthWise\Aug-2018\Suraj Ghimire_00001_part1_1-Aug-2018_5000.pdf, F:\invoice_Report\monthWise\Dec-2018\Karan Kumar_00002_part3_1-Dec-2018_2000.pdf, F:\invoice_Report\monthWise\Jan-2019\Karan Kumar_00002_part4_1-Jan-2019_1000.pdf, F:\invoice_Report\monthWise\Jan-2019\Karan Kumar_00002_part5_1-Jan-2019_1000.pdf, F:\invoice_Report\monthWise\Oct-2018\Karan Kumar_00002_part1_1-Oct-2018_2000.pdf, F:\invoice_Report\monthWise\Oct-2018\Karan Kumar_00002_part2_31-Oct-2018_2000.pdf, F:\invoice_Report\monthWise\Oct-2018\Suraj Ghimire_00001_part3_1-Oct-2018_8000.pdf, F:\invoice_Report\monthWise\Sep-2018\Suraj Ghimire_00001_part2_1-Sep-2018_7000.pdf)

  def getGSTData(fileList:ListBuffer[File]):ListBuffer[GstData]={
    val fList:ListBuffer[String] = fileList.map(x => x.getName.toString)  // will get the filename 1Ex: Suraj Ghimire_00001_part1_1-Aug-2018_5000.pdf
    val gstData:ListBuffer[GstData] = new ListBuffer[GstData]

    var S_No:String = null
    var invoiceNo:String = null
    var invoiceDate:String = null
    val CustomerGSTNO:String = "36AACCO7284M1ZU"
    val YourGSTNo:String = ""
    var TotalInvoiceValue:String= null
    var TaxableValue:String= null
    var CGST:String= null
    var SGST:String= null
    val IGST:String=""
    val StatePlaceofSupply:String = "Telengana"
    val percentage:String = "18%"

    fList.map( x => {
      invoiceNo = s"OLC-${x.split("_")(1)}-${x.split("_")(2).charAt(4)}"  //OLC-00001-1
      invoiceDate= x.split("_")(3)
      var emi = x.split("_")(4).split("\\.")(0).toFloat
      TotalInvoiceValue = emi.toString
      TaxableValue = (emi - (emi*0.18f)).toString
      CGST = (emi * 0.09f).toString
      SGST = (emi * 0.09f).toString

      gstData.append(GstData(S_No, invoiceNo, invoiceDate, CustomerGSTNO, YourGSTNo, TotalInvoiceValue, TaxableValue, CGST, SGST, IGST, StatePlaceofSupply, percentage))
    }) //1Ex O/P: GstData(OLC-00001-1,1-Aug-2018,5000.0)
    gstData
  } // 1Ex: Output: ListBuffer(GstData(OLC-00001-1,1-Aug-2018,5000.0),...

  def getGSTDataOFtheMonth(gstData:ListBuffer[GstData]):ListBuffer[ListBuffer[GstData]]={
    var monthDir:HashSet[String]= new HashSet[String]
    val gstDataForMMMUpper:ListBuffer[ListBuffer[GstData]] = new ListBuffer[ListBuffer[GstData]]
    var gstDataForMMMLower:ListBuffer[GstData]= null
    gstData.map((gst => { var temp = s"${gst.invoiceDate.split("-")(1)}-${gst.invoiceDate.split("-")(2)}"
      monthDir += temp
    })) // will get month in MMM-yyyy 1EX: Aug-2018
    val dirLists = monthDir.toList

    for(i <- 0 until dirLists.size ){
      gstDataForMMMLower = new ListBuffer[GstData]
      for(j <- 0 until gstData.size ){
        var gstDataMMM:String = s"${gstData(j).invoiceDate.split(("-"))(1)}-${gstData(j).invoiceDate.split(("-"))(2)}"
        var monthDirMMM:String = dirLists(i)
        if(gstDataMMM == monthDirMMM) {
          //gstData(j).sno = j.toString
          gstDataForMMMLower.append(gstData(j)) }
        //gstData(j).sno = j.toString
      }
      gstDataForMMMUpper.append(gstDataForMMMLower)
    }
    gstDataForMMMUpper
  } // Get All 5 DateFolder's List of data as a "ListBuffer(ListBuffer(GstData(OLC-00002-3,1-Dec-2018,2000.0)),....."

  def writeGSTDataToXLSFile(data:ListBuffer[ListBuffer[GstData]], directoryMMMyyyy:ListBuffer[String])={
    try {
      var month = ""
      for(i <- 0 until data.size){
        val workbook = new XSSFWorkbook
        val sheet = workbook.createSheet("MonthlyGSTReport")

        //Adding Header to EXCEL
        val header = Array("S.NO", "Invoice No.", "Invoice Date", "Customer GST NO", "Your GST No", "Total Invoice Value", "Taxable Value", "CGST", "SGST", "IGST", "State ( Place of Supply)", "%")

        val headerRow = sheet.createRow(0)
        for(h <- 0 until header.size){
          val font = workbook.createFont
          font.setFontHeightInPoints(10.toShort)
          font.setFontName("Arial")
          font.setBold(true)
          font.setColor(IndexedColors.RED.getIndex)
          val style = workbook.createCellStyle
          style.setFont(font)
          val cell = headerRow.createCell(h)
          cell.setCellValue(header(h).toString)
          cell.setCellStyle(style)
        } // Creating header font style

        for(j <- 0 until data(i).size){
          data(i)(j).sno = (j+1).toString
          month = s"${data(i)(j).invoiceDate.split("-")(1)}-${data(i)(j).invoiceDate.split("-")(2)}"
          val row = sheet.createRow(j+1)
          val itr = data(i)(j).productIterator
          var array = itr.toArray
          for(t <- 0 until array.size) {
            val cell = row.createCell(t)
            cell.setCellValue(array(t).toString)
          } //updates each Cell of the Row
        }// update and Increment Rows

        for(dir <- 0 until directoryMMMyyyy.size) {
          if (month == directoryMMMyyyy(dir)) {
            val outputStream = new FileOutputStream(s"E:\\invoice_Report\\monthWise\\$month\\MonthlyGSTReport-${month}.xlsx")
            workbook.write(outputStream)
            workbook.close()
          }
        }
      } // Create new WorkSheet of the month
    }catch {
      case e: FileNotFoundException =>
        e.printStackTrace()
      case e: IOException =>
        e.printStackTrace()
    }
  }
}