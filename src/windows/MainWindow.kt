package windows

import helpers.RegexHelper
import java.awt.Color
import java.awt.Dimension
import java.io.File
import java.io.FileInputStream
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.text.BadLocationException
import javax.swing.text.DefaultHighlighter
import java.io.BufferedInputStream
import java.util.regex.Pattern
import javax.swing.JScrollPane

class MainWindow : JFrame() {

    private val textBlock: JEditorPane
    private val btnFind: JButton
    private val btnOpenDialog: JButton
    private val fieldFileName:JTextField
    private val fieldSearchString: JTextField
    private val btnFindEmail:JButton
    private val btnSubs:JButton

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        minimumSize = Dimension(500, 500)
        textBlock = JEditorPane()
        fieldFileName = JTextField()
        fieldSearchString= JTextField()
        fieldFileName.isEditable = false
        fieldFileName.background = Color.white
        btnFind = JButton()

        btnFind.addActionListener{
            val regex = Regex("[0-9~`!@#$%^&*+=.]")
            if (regex.containsMatchIn("^["+fieldFileName.text+"]$")){
                find(fieldSearchString.text)
            } else {
                find("([a-zA-Zа-яА-ЯёЁ]*)"+fieldSearchString.text+"([a-zA-Zа-яА-ЯёЁ]*)")
            }

        }
        btnFindEmail = JButton()
        btnFindEmail.text = "Найти e-mail"
        btnOpenDialog = JButton()
        btnOpenDialog.text = "Открыть файл"
        btnFind.text = "Найти"
        btnFindEmail.addActionListener {
            find("([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})")
        }
        btnSubs = JButton()
        btnSubs.text="Заменить"
        val scroll = JScrollPane(
            textBlock,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        )
        btnOpenDialog.addActionListener {
            val filefilter = FileNameExtensionFilter("TXT", "txt")
            val d = JFileChooser()
            d.isAcceptAllFileFilterUsed = false
            d.fileFilter = filefilter
            d.currentDirectory = File(".")
            d.dialogTitle = "Выберите файл"
            d.approveButtonText = "Выбрать"
            d.addChoosableFileFilter(filefilter)
            d.fileSelectionMode = JFileChooser.FILES_ONLY
            val result = d.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                textBlock.text = ""
                fieldFileName.text = d.selectedFile.name
                val fileInputStream = FileInputStream(d.selectedFile)
                val bufferedInputStream = BufferedInputStream(fileInputStream, 200)
                var i = bufferedInputStream.read()
                do {
                    textBlock.text += i.toChar()
                    i = bufferedInputStream.read()
                } while (i != -1)


            }
        }
        btnSubs.addActionListener{
            find2("(https?:\\/\\/)?([\\w-]{1,32}\\.[\\w-]{1,32})[^\\s@]*")
        }

        val gl = GroupLayout(contentPane)
        layout = gl
        gl.setHorizontalGroup(
            gl.createSequentialGroup()
                .addGap(4)
                .addGroup(
                    gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(
                            gl.createSequentialGroup()
                                .addComponent(
                                    fieldFileName
                                )
                                .addGap(4)
                                .addComponent(
                                    btnOpenDialog,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE
                                )
                                .addGap(4)
                        )
                        .addComponent(scroll, 450, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                        .addGroup(
                            gl.createSequentialGroup()
                                .addComponent(
                                    fieldSearchString
                                )
                                .addGap(4)
                                .addComponent(
                                    btnFind,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE
                                )
                                .addGap(4)
                        )
                        .addGap(4)
                        .addComponent(
                            btnFindEmail,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE
                        )
                        .addGap(4)
                        .addGroup(
                            gl.createSequentialGroup()
                                .addGap(4)
                                .addComponent(
                                    btnSubs,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.PREFERRED_SIZE
                                )
                                .addGap(4)
                        )
                        .addGap(4)
                ))
        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGap(4)
                .addGroup(
                    gl.createParallelGroup()
                        .addComponent(
                            fieldFileName
                        )
                        .addComponent(
                            btnOpenDialog,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE
                        )
                )
                .addGap(4)
                .addComponent(scroll, 400, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(4)
                .addGap(4)
                .addGroup(
                    gl.createParallelGroup()
                        .addComponent(
                            fieldSearchString
                        )
                        .addComponent(
                            btnFind,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE
                        )

                )
                .addGap(4)
                .addComponent(
                    btnFindEmail,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE
                )
                .addGap(4)
                .addGroup(
                    gl.createParallelGroup()
                        .addComponent(
                            btnSubs,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE
                        )
                )
                .addGap(4)
        )

        pack()
    }

    private fun find(str:String) {
        val rh = RegexHelper()
        rh.regex = str
        var txt = textBlock.text
        txt = txt.replace("\r", "")
        val result = rh.findIn(txt)
        val h = textBlock.highlighter
        val hp = DefaultHighlighter
            .DefaultHighlightPainter(Color.YELLOW)
        h.removeAllHighlights()
        for (res in result) {
            try {
                h.addHighlight(res.first, res.second, hp)
            } catch (e: BadLocationException) {
            }
        }
    }

    private fun find2(str:String) {
        val rh = RegexHelper()
        rh.regex = str
        var txt = textBlock.text
        txt = txt.replace("\r", "\n")
        val result = rh.findIn(txt)
        val h = StringBuilder(textBlock.text)
        for (res in result) {
            try {
                if((res.second-res.first)>=40){
                    for (i in res.first+10 until res.second-10) {
                        if (i>=(res.second-13)) {
                            h.setCharAt(i,'*')
                        }else {
                            h.setCharAt(i,'\u0000')
                        }
                    }
                }
                textBlock.text = h.toString()
            } catch (e: BadLocationException) {
            }
        }
    }

    /*private fun subs (oldStr:String,newStr:String){
        val rh = RegexHelper()
        rh.regex = oldStr
        var txt = textBlock.text
        txt = txt.replace(oldStr,newStr)
        val res = rh.findIn(txt)
    }*/
}

