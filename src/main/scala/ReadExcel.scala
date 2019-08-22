import scala.collection.mutable.ListBuffer
import java.io.{File, FileInputStream, FileNotFoundException, IOException}
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ReadExcel {

  def readExceltoList(FILE_NAME: String): ListBuffer[ListBuffer[String]] = {

    var addList: ListBuffer[String] = null
    val upperList: ListBuffer[ListBuffer[String]] = new ListBuffer[ListBuffer[String]]

    try {
      val excelFile = new FileInputStream(new File(FILE_NAME))
      val workbook = new XSSFWorkbook(excelFile)
      val datatypeSheet = workbook.getSheetAt(0)
      val dataSheetIterator = datatypeSheet.iterator
      while (dataSheetIterator.hasNext) {
        addList = new ListBuffer[String]
        val currentRow = dataSheetIterator.next
        if (currentRow.getRowNum != 0) {
          for (i <- 0 until currentRow.getLastCellNum) addList.append(currentRow.getCell(i).toString)
          upperList.append(addList)
        }
      }
    } catch {
      case e: FileNotFoundException => e.printStackTrace()
      case e: IOException => e.printStackTrace()
      case e: Exception => e.printStackTrace()
    }
    upperList

  }
}
