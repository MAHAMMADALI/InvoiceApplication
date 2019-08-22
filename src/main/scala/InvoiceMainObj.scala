import java.io.File

import scala.collection.mutable.ListBuffer

object InvoiceMainObj {
  private val RAW_FILE:String = "E:\\BigData\\OLC\\workspace\\scalaExamples\\dataset\\rawfile.xlsx"
  private val DIR_PATH:String = "E:\\invoice_Report\\monthWise"
  def main(args: Array[String]): Unit = {

    /* --------- Read the XL file's rows and creat a ListOfList Object-------------
    ListBuffer(
    ListBuffer(1.0, Suraj Ghimire, suraz.hadoop@gmail.com, 903 241 2236, 1234 1234 1234, Core Java, 20000.0, 5000.0, 01-Aug-2018, 7000.0, 01-Sep-2018, 8000.0, 01-Oct-2018),
    ListBuffer(2.0, Karan Kumar, karan@gmail.com, 903 241 2237, 1234 1234 1234 , Scala, 7000.0, 2000.0, 01-Oct-2018, 2000.0, 31-Oct-2018, 2000.0, 01-Dec-2018, 1000.0, 01-Jan-2019, 1000.0, 01-Jan-2019))
    ----------------------*/
    val readXL = new ReadExcel
    val studentList = readXL.readExceltoList(RAW_FILE)

    /* ------ Reads the ListOfList Obj and featch into Student[Payment(Emi,Date)] Obj -------
    Student(1,Suraj Ghimire,suraz.hadoop@gmail.com,903 241 2236,1234 1234 1234,Core Java,20000.0,Payment(ListBuffer(5000.0, 7000.0, 8000.0),ListBuffer(Wed Aug 01 00:00:00 IST 2018, Sat Sep 01 00:00:00 IST 2018, Mon Oct 01 00:00:00 IST 2018)))
    Student(2,Karan Kumar,karan@gmail.com,903 241 2237,1234 1234 1234 ,Scala,7000.0,Payment(ListBuffer(2000.0, 2000.0, 2000.0, 1000.0, 1000.0),ListBuffer(Mon Oct 01 00:00:00 IST 2018, Wed Oct 31 00:00:00 IST 2018, Sat Dec 01 00:00:00 IST 2018, Tue Jan 01 00:00:00 IST 2019, Tue Jan 01 00:00:00 IST 2019)))
    ----------------------*/
    val stu = new StudentInfo
    val stuDetails = stu.studentDetails(studentList)

    /* -------- Reads the Student's DATA and generate FileNames ( with added phoneNumber and courceTaken fields)
    ListBuffer(ListBuffer(1.0, Suraj Ghimire, suraz.hadoop@gmail.com, 903 241 2236, 1234 1234 1234, Core Java, 20000.0, 5000.0, 01-Aug-2018, 7000.0, 01-Sep-2018, 8000.0, 01-Oct-2018), ListBuffer(2.0, Karan Kumar, karan@gmail.com, 903 241 2237, 1234 1234 1234 , Scala, 7000.0, 2000.0, 01-Oct-2018, 2000.0, 31-Oct-2018, 2000.0, 01-Dec-2018, 1000.0, 01-Jan-2019, 1000.0, 01-Jan-2019))
    ListBuffer(ListBuffer(Suraj Ghimire_00001_part1_1-Aug-2018_5000,903 241 2236,Core Java, Suraj Ghimire_00001_part2_1-Sep-2018_7000,903 241 2236,Core Java, Suraj Ghimire_00001_part3_1-Oct-2018_8000,903 241 2236,Core Java), ListBuffer(Karan Kumar_00002_part1_1-Oct-2018_2000,903 241 2237,Scala, Karan Kumar_00002_part2_31-Oct-2018_2000,903 241 2237,Scala, Karan Kumar_00002_part3_1-Dec-2018_2000,903 241 2237,Scala, Karan Kumar_00002_part4_1-Jan-2019_1000,903 241 2237,Scala, Karan Kumar_00002_part5_1-Jan-2019_1000,903 241 2237,Scala))
     ----------------------*/
    val fileName = new FileName
    val fileNames = fileName.fileNameDesign(stuDetails)

    /*-------------- WriteToPDF --------------
    1. getDirWithName() takes above FileName Details and give InvoiceBy StudentWise Folder names
    ListBuffer(Suraj Ghimire_OLC00001, Karan Kumar_OLC00002)

    2.  getDirWithMMMyyyy() Similarlly InvoiceBy MonthWise folder
    ListBuffer(Aug-2018, Sep-2018, Oct-2018, Dec-2018, Jan-2019)

    3. writeInvoiceDetailtoPDF() takes StudentFolderNames, DateFoldernames, InvoiceFiles and
    Generats Respective PDFs under StudentWise and MonthWise folders
    ----------------------------------*/
    val pdfObj = new WriteToPDF
    val directoryName = pdfObj.getDirWithName(fileNames)
    val directoryMMMyyyy = pdfObj.getDirWithMMMyyyy(fileNames)
    pdfObj.writeInvoiceDetailtoPDF(fileNames, directoryName, directoryMMMyyyy)

    /* -------------- MonthlyGSTReportData ------------
    1. getFiles() will get the List of files under the parent "Dir: F:\invoice_Report\monthWise" recurcively
    ListBuffer(
    F:\invoice_Report\monthWise\Aug-2018\Suraj Ghimire_00001_part1_1-Aug-2018_5000.pdf,
    F:\invoice_Report\monthWise\Dec-2018\Karan Kumar_00002_part3_1-Dec-2018_2000.pdf,
    F:\invoice_Report\monthWise\Jan-2019\Karan Kumar_00002_part4_1-Jan-2019_1000.pdf,
    F:\invoice_Report\monthWise\Jan-2019\Karan Kumar_00002_part5_1-Jan-2019_1000.pdf,
    F:\invoice_Report\monthWise\Oct-2018\Karan Kumar_00002_part1_1-Oct-2018_2000.pdf,
    F:\invoice_Report\monthWise\Oct-2018\Karan Kumar_00002_part2_31-Oct-2018_2000.pdf,
    F:\invoice_Report\monthWise\Oct-2018\Suraj Ghimire_00001_part3_1-Oct-2018_8000.pdf,
    F:\invoice_Report\monthWise\Sep-2018\Suraj Ghimire_00001_part2_1-Sep-2018_7000.pdf)

    2. getGSTData() takes fileList and update GSTData Objects
    ListBuffer(
    GstData(null,OLC-00001-1,1-Aug-2018,36AACCO7284M1ZU,,5000.0,4100.0,450.00003,450.00003,,Telengana,18%),
    GstData(null,OLC-00002-3,1-Dec-2018,36AACCO7284M1ZU,,2000.0,1640.0,180.0,180.0,,Telengana,18%),
    GstData(null,OLC-00002-4,1-Jan-2019,36AACCO7284M1ZU,,1000.0,820.0,90.0,90.0,,Telengana,18%),
    GstData(null,OLC-00002-5,1-Jan-2019,36AACCO7284M1ZU,,1000.0,820.0,90.0,90.0,,Telengana,18%),
    GstData(null,OLC-00002-1,1-Oct-2018,36AACCO7284M1ZU,,2000.0,1640.0,180.0,180.0,,Telengana,18%),
    GstData(null,OLC-00002-2,31-Oct-2018,36AACCO7284M1ZU,,2000.0,1640.0,180.0,180.0,,Telengana,18%),
    GstData(null,OLC-00001-3,1-Oct-2018,36AACCO7284M1ZU,,8000.0,6560.0,720.0,720.0,,Telengana,18%),
    GstData(null,OLC-00001-2,1-Sep-2018,36AACCO7284M1ZU,,7000.0,5740.0,630.0,630.0,,Telengana,18%))

    3. getGSTDataOFtheMonth(gstData) and return GSTData in monthWise as a list
    ListBuffer(
    ListBuffer(GstData(null,OLC-00002-3,1-Dec-2018,36AACCO7284M1ZU,,2000.0,1640.0,180.0,180.0,,Telengana,18%)),
    ListBuffer(GstData(null,OLC-00002-4,1-Jan-2019,36AACCO7284M1ZU,,1000.0,820.0,90.0,90.0,,Telengana,18%), GstData(null,OLC-00002-5,1-Jan-2019,36AACCO7284M1ZU,,1000.0,820.0,90.0,90.0,,Telengana,18%)),
    ListBuffer(GstData(null,OLC-00001-2,1-Sep-2018,36AACCO7284M1ZU,,7000.0,5740.0,630.0,630.0,,Telengana,18%)),
    ListBuffer(GstData(null,OLC-00001-1,1-Aug-2018,36AACCO7284M1ZU,,5000.0,4100.0,450.00003,450.00003,,Telengana,18%)),
    ListBuffer(GstData(null,OLC-00002-1,1-Oct-2018,36AACCO7284M1ZU,,2000.0,1640.0,180.0,180.0,,Telengana,18%), GstData(null,OLC-00002-2,31-Oct-2018,36AACCO7284M1ZU,,2000.0,1640.0,180.0,180.0,,Telengana,18%), GstData(null,OLC-00001-3,1-Oct-2018,36AACCO7284M1ZU,,8000.0,6560.0,720.0,720.0,,Telengana,18%))
    )

    4. writeGSTDataToXLSFile(montlyGSTFileData,directoryMMMyyyy) method
    will Create and Writes above data inthe XLS files by Calculating GST  */
    val montlyGSTReportData = new MonthlyGSTReportData

    val fileList:ListBuffer[File] = new ListBuffer[File]
    montlyGSTReportData.getFiles(DIR_PATH,fileList)

    val gstData = montlyGSTReportData.getGSTData(fileList)

    val montlyGSTFileData = montlyGSTReportData.getGSTDataOFtheMonth(gstData)  // get all the 5 Date Dir's as ListBuffer(ListBuffer(GstData(OLC-00002-3,1-Dec-2018,2000.0))...

    montlyGSTReportData.writeGSTDataToXLSFile(montlyGSTFileData,directoryMMMyyyy)

  }
}
