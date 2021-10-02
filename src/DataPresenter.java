
import java.io.IOException;
import java.util.*;

/**
 * Klasa której zadaniem jest prezentacja danych w formie wygenerowanej strony html.
 * @author Marek Popowicz, Grudzień 2018.
 */
public class DataPresenter {
  
    private final WorkingTimeCalendar wtc;
    private final String[] data;
    private final DataWriterReader dataWR;
    private final  Calendar date = Calendar.getInstance(Locale.getDefault());
    private boolean considerFreeTime;

    public DataPresenter(String workingTimeMode, String filePath, boolean considerFreeTime) throws IOException {
        this.considerFreeTime = considerFreeTime;
        this.wtc = new WorkingTimeCalendar(workingTimeMode, date, filePath, considerFreeTime);
        this.dataWR = new DataWriterReader();
        this.data = dataWR.readDataFromFile(filePath);
    }

    public DataPresenter(String workingTimeMode, int customMonth, String filePath, boolean considerFreeTime) throws IOException {
        this.considerFreeTime = considerFreeTime;
        this.wtc = new WorkingTimeCalendar(workingTimeMode, customMonth, filePath, considerFreeTime);
        this.dataWR = new DataWriterReader();
        this.data = dataWR.readDataFromFile(filePath);
    }

    private String[] prepareDocument() {
     String[] weekDays = wtc.getWeekDays();
     int[] monthDays = wtc.getMonthDays();
     double[] workDays = wtc.getWorkDays();
     int[] freeDays = wtc.getFreeDays();
     double unitTimePerHour = wtc.getUnitWorkTimePerHour();

    List<String> documentLines = new ArrayList<>();
    String[] document = null;
    
    documentLines.add("<!doctype html>");
    documentLines.add("<html lang=\"pl\">");
    documentLines.add("<head>");
     
//meta
    documentLines.add("  <meta charset=\"utf-8\">");
    documentLines.add("  <title>Roboczogodziny " + wtc.getNameOfMonth() + "</title>");
    documentLines.add("  <meta name=\"Arkusz roboczogodzn\" content=\"Roboczogodziny\">");
    documentLines.add("  <meta name=\"Marek Popowicz\" content=\"dokument html\">");
    
//style 
    documentLines.add("\n");
    documentLines.add("  <style>");
    documentLines.add("  .button {");
    documentLines.add("     border: none; color: white; padding: 2px 4px; text-align: center;");
    documentLines.add("     font-size: 10px;cursor: pointer; border-radius: 5px; }");
    documentLines.add("  .inactive {");
    documentLines.add("     color: orangered;}");
    documentLines.add("  .taskNo {");
    documentLines.add("     text-shadow: 1px 2px 3px rgba(176,176,176,0.93);}");
    documentLines.add("  .closed {");
    documentLines.add("     background-color: aliceblue;}");
    documentLines.add("  .workDays {");
    documentLines.add("     color: black; text-shadow: 1px 2px 3px rgba(176,176,176,0.93); background-color: aliceblue; }");
    documentLines.add("  .workDayTimeSum {");
    documentLines.add("     font-size: 0.8em;font-weight: bold; font-family: Tahoma; }");
    documentLines.add("  table {");
    documentLines.add("     border: 2px solid black; border-spacing: 5px;");
    documentLines.add("     border-collapse: collapse; font-family: Arial}");
    documentLines.add("  th, td {");
    documentLines.add("     border: 1px solid black; border-spacing: 5px;");
    documentLines.add("     border-collapse: collapse; font-family: Arial}");
    documentLines.add("  td {");
    documentLines.add("     padding: 3px; text-align: center; font-size: 0.8em;}");
    documentLines.add("  #header {");
    documentLines.add("     text-shadow: 1px 2px 3px rgba(176,176,176,0.93); width: 80%;");
    documentLines.add("     margin: auto; padding-bottom: 20px; padding-top: 20px;}");
    documentLines.add("  .foot {");
    documentLines.add("     text-shadow: 1px 2px 3px rgba(176,176,176,0.93); background-color: aliceblue;}");
    documentLines.add("  thead {");
    documentLines.add("     text-shadow: 1px 2px 3px rgba(176,176,176,0.93); background-color: aliceblue;}");
    documentLines.add("  body {");
    documentLines.add("  margin: 30px; background-color: ghostwhite;}");
    documentLines.add("  a:link { color:  black; text-decoration: none; }");
    documentLines.add("  a:hover { font-weight:  bold; color: orangered; }");
    documentLines.add("  tr:hover { background-color: beige;}");
    documentLines.add("  tr:nth-child(even) {background-color: floralwhite;}");
    documentLines.add("  </style>");

//script
    documentLines.add("\n");
    documentLines.add("  <script>");
    documentLines.add("     var monthWorkDays = [];");
    documentLines.add("     var monthWorkDaySum=0;");
    documentLines.add("     var textToSave = [];");
    documentLines.add("     function changeValue(task, id) {");
    documentLines.add("         var time = prompt(\"Podaj czas pracy dla zadania: \" + task);");
    documentLines.add("         var cell = document.getElementById(id);");
    documentLines.add("         time = time.replace(/,/g, '.');");
    documentLines.add("         var checked = checkValue(time);");
    documentLines.add("         if(checked!=null) cell.innerHTML = checked;");
    documentLines.add("         cell.style.fontWeight=\"bold\";");
    documentLines.add("         countValues(id);}");
    
    documentLines.add("\n");
    documentLines.add("     function signIt() {");
    documentLines.add("         var txt = prompt(\"Podaj swoje imię i nazwisko:\");");
    documentLines.add("         document.getElementById(\"employee\").innerHTML = txt;};");
    
    documentLines.add("\n");
    documentLines.add("     function reset() {");
    documentLines.add("         var acitve_cells = document.querySelectorAll('.active');");
    documentLines.add("         acitve_cells.forEach(function(element) {");
    documentLines.add("         element.style.fontWeight=\"normal\";");    
    documentLines.add("         element.innerHTML =\"0.0\";});");
    documentLines.add("         setWorkDaysTimeSum();");
    documentLines.add("         for (x=0; x<  monthWorkDays.length; x++) {monthWorkDays[x]=undefined;};");
    documentLines.add("         document.getElementById('labourTotalTime').innerHTML = monthWorkDaySum};");
    
    documentLines.add("\n");
    documentLines.add("     function setWorkDaysTimeSum() {");
    documentLines.add("         var workDaysTime = document.querySelectorAll('th.workDays > span');");
    documentLines.add("         var workdaysTimeSum = document.querySelectorAll('td.workDayTimeSum');");
    documentLines.add("         for (i=0; i < workDaysTime.length; i++) {");
    documentLines.add("             if(workDaysTime[i].innerHTML =='✘'){");
    documentLines.add("                 var dayNo = workDaysTime[i].id.substring(workDaysTime[i].id.indexOf(\"_\")+1);");
    documentLines.add("                 workDaysTime[i].innerHTML =  monthWorkDays[dayNo].toFixed(1); }");
    documentLines.add("             workdaysTimeSum[i].innerHTML = workDaysTime[i].innerHTML;};");
    documentLines.add("      };");
    
    documentLines.add("\n");
    documentLines.add("     function checkValue(value) {");
    documentLines.add("         if (value ==null) return;");
    documentLines.add("         if(isNaN(Number(value))) return null;");
    documentLines.add("         else {");
    documentLines.add("         var num = Number(Math.abs(value));");
    documentLines.add("         return round(num,1);}}");    
    
    documentLines.add("\n");
    documentLines.add("     function changeTaskNo(id, task) {");
    documentLines.add("         var txt = prompt(\"Numer zadania:\", task); txt = txt.trim();");
    documentLines.add("         if(txt != null && txt!='')");
    documentLines.add("         document.getElementById(id).innerHTML = txt;};");
    
    documentLines.add("\n");
    documentLines.add("     function getElementValue(id) {");
    documentLines.add("         return document.getElementById(id).innerHTML;};");
    
    documentLines.add("\n");
    documentLines.add("     function countValues(id) {");
    documentLines.add("         var collIndex = id.substring(id.indexOf(\"-\")+1);");
    documentLines.add("         var sum = 0, result = 0;");
    documentLines.add("         var dayHours = parseFloat(document.getElementById('workDay_' + collIndex).innerHTML);");
    documentLines.add("         var timeToSum = document.getElementsByClassName('row');");
    documentLines.add("              for (x = 0; x<timeToSum.length; x++) {");
    documentLines.add("                 var cell = timeToSum[x].firstChild.firstElementChild;");
    documentLines.add("                 var currentCellIndex = parseInt(cell.id.substring(cell.id.indexOf(\"-\")+1));");
    documentLines.add("                 if(currentCellIndex == collIndex) sum += parseFloat(cell.innerText);");
    documentLines.add("              };");
    documentLines.add("         result = dayHours - sum.toFixed(2);");
    documentLines.add("         document.getElementById('sum_' + collIndex).innerHTML = round(result,1);");
    documentLines.add("         };");
    documentLines.add("\n");
    documentLines.add("     function round(value, decimals) {");
    documentLines.add("         return Number(Math.round(value +'e'+ decimals) +'e-'+ decimals).toFixed(decimals);}");
    
    documentLines.add("\n");
    documentLines.add("     function addTableRow(workDays) {");
    documentLines.add("         var lastRow = parseInt(document.getElementById(\"totalOrderNo\").innerHTML);");
    documentLines.add("         var newRow = document.getElementById(\"tabela\").insertRow(lastRow+3); ");
    documentLines.add("         addElementAttrybute(newRow, 'id', 'row_id_' + (lastRow+1));");
    documentLines.add("         for (i=0; i < workDays.length+3; i++) {");
    documentLines.add("             var cell = addTableCell(newRow, i);");//class='opened' default
    documentLines.add("             if(i == 0) { ");
    documentLines.add("                 cell = addElementText(cell, lastRow+1);");
    documentLines.add("                 addElementAttrybute(cell, 'id', 'lp_id_' + lastRow);"); 
    documentLines.add("                 addElementAttrybute(cell, 'class', 'closed');");
    documentLines.add("             };");
    documentLines.add("             if(i == 1) {"); 
    documentLines.add("                   var _element = createChildElement(cell, 'BUTTON', 'deleteButton', 'button');");
    documentLines.add("                   cell.style.borderRight = \"none\";"); 
    documentLines.add("                   _element.style.backgroundColor = \"red\";");
    documentLines.add("                   addElementText(_element, '\\u2716');");
    documentLines.add("                   addElementAttrybute(_element, 'title', 'Usuń zadanie');");
    documentLines.add("                   addElementAttrybute(_element, 'onclick', 'deleteTableRow('+ (lastRow+1) +')');");
    documentLines.add("             };");
    documentLines.add("             if(i == 2) {");
    documentLines.add("                   cell = createLink(cell, \"javascript:changeTaskNo('task_\" + lastRow + \"', document.getElementById('task_\"+ lastRow + \"').innerHTML);\", 'Numer zadania');");     
    documentLines.add("                   cell = addElementAttrybute(cell, 'id', 'task_id_' + lastRow); ");
    documentLines.add("                   var _element = addElementAttrybute(cell, 'class', 'opened');");    
    documentLines.add("                   cell.style.textAlign = \"left\";");
    documentLines.add("                   _element = createChildElement(cell.lastChild, 'SPAN', 'task_'+lastRow, 'taskNo');");
    documentLines.add("                   addElementText(_element, 'I-WR-');");
    documentLines.add("                   cell.style.borderLeft = \"none\";");
    documentLines.add("             }");
    documentLines.add("             if(i > 2) {"); 
    documentLines.add("                  addElementAttrybute(cell, 'id', 'day_id_' + lastRow); "); 
    documentLines.add("                  if (workDays[i-3]>0) {"); 
    documentLines.add("                     cell = createLink(cell, \"javascript:changeValue(document.getElementById('task_\" + (lastRow) + \"').innerHTML, 'cell_\" + (lastRow+1)  + '-' + (i-2) + \"');\");");
    documentLines.add("                     cell = addElementAttrybute(cell, 'class', 'opened row');");
    documentLines.add("                     _element = createChildElement(cell.lastChild, 'SPAN', 'cell_' + (lastRow+1) + '-' + (i-2) , 'active');");  
    documentLines.add("                     if (monthWorkDays[i-2]!=undefined && workDays[i-3] > 0) addElementText(_element, '')"); 
    documentLines.add("                     else addElementText(_element, '0.0');}");
    documentLines.add("                     else if(workDays[i-3]==0) { cell = addElementText(cell, ''); addElementAttrybute(cell, 'class', 'closed');};");    
    documentLines.add("                     };");
    documentLines.add("             document.getElementById(\"totalOrderNo\").innerHTML=lastRow+1;");
    documentLines.add("         };");
    documentLines.add("     };");   

    documentLines.add("\n");
    documentLines.add("     function addTableCell(_row, _cellPosition, _id, _class) { ");
    documentLines.add("         var _cell = _row.insertCell(_cellPosition);"); 
    documentLines.add("         if(_id != undefined) _cell.setAttribute('id', _attrybuteValue);"); 
    documentLines.add("         if(_class != undefined) _cell.setAttribute('class', _class); else _cell.setAttribute('class', 'opened');");    
    documentLines.add("         return _cell;};");
    documentLines.add("\n");
    
    documentLines.add("     function addElementAttrybute(_element, _attrybuteType, _attrybuteValue) { ");    
    documentLines.add("         if(_attrybuteValue != undefined) _element.setAttribute(_attrybuteType, _attrybuteValue);");
    documentLines.add("         return _element;};");
    documentLines.add("\n");
    
    documentLines.add("     function addElementText(_element, _text) { ");    
    documentLines.add("         if(_text != undefined) _element.appendChild(document.createTextNode(_text));");
    documentLines.add("         return _element;};"); 
    documentLines.add("\n");
    
    documentLines.add("     function createLink(_parent, _href, _title) {");
    documentLines.add("         var a = document.createElement('a');");
    documentLines.add("         a.href = _href;");
    documentLines.add("         if(_title != undefined) a.title = _title;");
    documentLines.add("         if(_parent != undefined) _parent.appendChild(a);");
    documentLines.add("         return _parent;};"); 
    documentLines.add("\n");
    
    documentLines.add("     function createChildElement(_parent, _elementType, _elementID, _elementClass) { ");
    documentLines.add("         var _element = document.createElement(_elementType);");
    documentLines.add("         if(_elementID != undefined) _element = addElementAttrybute(_element, 'id', _elementID);");
    documentLines.add("         if(_elementClass != undefined) _element = addElementAttrybute(_element, 'class', _elementClass);");
    documentLines.add("         if(_parent != undefined && _element != undefined) _parent.appendChild(_element);");
    documentLines.add("         return _element;};"); 
    documentLines.add("\n");
    
    documentLines.add("     function deleteTableRow(id) {");
    documentLines.add("         var totalOrders = parseInt(document.getElementById(\"totalOrderNo\").innerHTML);");
    documentLines.add("         var row = document.getElementById('row_id_'+id);");
    documentLines.add("         var table = row.parentNode;");
    documentLines.add("             while ( table && table.tagName != 'TABLE' )");
    documentLines.add("             table = table.parentNode;"); //odświerzenie węzłów tabeli !!!
    documentLines.add("             if ( !table ) return;");
    documentLines.add("         table.deleteRow(row.rowIndex);");
    documentLines.add("         document.getElementById(\"totalOrderNo\").innerHTML=totalOrders-1;");
    documentLines.add("         recountTaks(); ");
    documentLines.add("         recountHours(); ");
    documentLines.add("         }; ");
    
    documentLines.add("\n"); 
    documentLines.add("     function recountTaks() {"); // Przeliczenie na nowo kolumny z liczbą porządkową
    documentLines.add("          var table = document.getElementById(\"tabela\");");
    documentLines.add("          for (i = 3; i<table.rows.length-1; i++) {");
    documentLines.add("             table.rows[i].cells[0].innerHTML = i-2;");
    documentLines.add("          };");
    documentLines.add("      };");
    
    documentLines.add("\n"); 
    documentLines.add("     function recountHours() {"); 
    documentLines.add("          var workdayHours = document.getElementsByClassName('workDays');");
    documentLines.add("          for(i=0; i<workdayHours.length; i++){");
    documentLines.add("              var currentDay = workdayHours[i].firstChild;"); 
    documentLines.add("              var DayId = currentDay.id.substring(currentDay.id.indexOf(\"_\")+1);");
    documentLines.add("              if(monthWorkDays[parseInt(DayId)]==undefined) countValues('cell_x-' + DayId);");
    documentLines.add("          };");
    documentLines.add("      };");
    
    documentLines.add("\n"); 
    documentLines.add("     function makeAbsence(day) {"); 
    documentLines.add("         var sum =  document.getElementById('sum_' + day);");
    documentLines.add("         var dayHours = document.getElementById('workDay_' + day);");
    documentLines.add("         var activeCells = document.getElementsByClassName('row');"); 
    documentLines.add("         var flag = monthWorkDays[day];");
    documentLines.add("         for (y = 0; y<activeCells.length; y++) {");
    documentLines.add("             var cell = activeCells[y].firstChild.firstElementChild;");
    documentLines.add("             var currentCellIndex = parseInt(cell.id.substring(cell.id.indexOf(\"-\")+1));");
    documentLines.add("             if(currentCellIndex == day && flag == undefined) cell.innerText=\"\"; ");
    documentLines.add("             if(currentCellIndex == day && flag != undefined) cell.innerText= '0.0';"); 
    documentLines.add("             cell.style.fontWeight=\"normal\";};");
    documentLines.add("         if(monthWorkDays[day] == undefined) {");
    documentLines.add("             monthWorkDays[day] = parseFloat(dayHours.innerHTML); ");
    documentLines.add("             dayHours.innerHTML = '&#10008;' ");
    documentLines.add("             sum.innerHTML='&#10008;';");
    documentLines.add("         }"); 
    documentLines.add("         else {");
    documentLines.add("             dayHours.innerHTML = monthWorkDays[day].toFixed(1);");
    documentLines.add("             sum.innerHTML=monthWorkDays[day].toFixed(1);");
    documentLines.add("             monthWorkDays[day] = undefined; ");
    documentLines.add("         }");
    documentLines.add("         updateTotalMonthTime(day);"); 
    documentLines.add("     }"); 

    documentLines.add("\n");  
    documentLines.add("     function updateTotalMonthTime(day) {"); 
    documentLines.add("         var totalMonthTime = document.getElementById('labourTotalTime');"); 
    documentLines.add("         var result;"); 
    documentLines.add("         if (monthWorkDays[day] != undefined) result = parseFloat(totalMonthTime.innerHTML) - monthWorkDays[day]"); 
    documentLines.add("         else result = parseFloat(totalMonthTime.innerHTML) + parseFloat(document.getElementById('workDay_' + day).innerHTML);"); 
    documentLines.add("         totalMonthTime.innerHTML = result.toFixed(1);"); 
    documentLines.add("     }"); 
    
    documentLines.add("\n");   
    documentLines.add("     function createFile() {");
    documentLines.add("         var employee = document.getElementById(\"employee\").innerHTML;");
    documentLines.add("         if(employee == '') { window.alert('Proszę podpisać kartę imieniem i nazwiskiem'); return };");
    documentLines.add("         var text = '';"); 
    documentLines.add("          for(t=0;t<textToSave.length; t++) { text += textToSave[t];}"); 
    documentLines.add("         blob = new Blob([text], { type: 'text/plain' }),"); 
    documentLines.add("         anchor = document.createElement('a');"); 
    documentLines.add("         anchor.download = 'Roboczogodziny ' + employee + '.csv';"); 
    documentLines.add("         anchor.href = (window.webkitURL || window.URL).createObjectURL(blob);"); 
    documentLines.add("         anchor.dataset.downloadurl = ['text/plain', anchor.download, anchor.href].join(':');"); 
    documentLines.add("         document.body.appendChild(anchor);"); 
    documentLines.add("         anchor.click();"); 
    documentLines.add("         document.body.removeChild(anchor);"); 
    documentLines.add("     }"); 
    documentLines.add("\n");
    
    documentLines.add("     function prepareDataToSave(cols){");
    documentLines.add("         var totalOrders = parseInt(document.getElementById('totalOrderNo').innerHTML);");
    documentLines.add("         var weekDays = document.getElementById('tableHeader');");
    documentLines.add("         var totalMonthTime = document.getElementById('labourTotalTime').innerHTML;"); 
    documentLines.add("         var workHours = document.getElementById('workDaysHours');"); 
    documentLines.add("         var monthName = weekDays.cells[1].innerText;"); 
    documentLines.add("         var summary = document.getElementsByClassName('foot');");
    documentLines.add("         var headerLine_0 = 'Liczba godzin: ' + totalMonthTime + ';';");
    documentLines.add("         for(h0=0; h0<workHours.cells.length; h0++){"); 
    documentLines.add("            if(workHours.cells[h0].innerText == '✘') headerLine_0 += 'x;'"); 
    documentLines.add("            else headerLine_0 += workHours.cells[h0].innerText + ';';"); 
    documentLines.add("         };"); 
    documentLines.add("         var headerLine_1 = monthName.substring(0, monthName.indexOf(' ')) + ' ' + new Date().getFullYear() +';';");
    documentLines.add("         for(h1=2; h1<weekDays.cells.length; h1++){"); 
    documentLines.add("             headerLine_1 += weekDays.cells[h1].innerText + ';';"); 
    documentLines.add("         };"); 
    documentLines.add("         var headerLine_2 = '' + ';';");
    documentLines.add("         for(h2=1; h2<cols+1; h2++){"); 
    documentLines.add("              headerLine_2 += (h2) + ';';"); 
    documentLines.add("         };"); 
    documentLines.add("         textToSave[0] = headerLine_0.replace(/\\./g,',') + '\\n';"); 
    documentLines.add("         textToSave[1] = headerLine_1.replace(/Śr/g,'Sr') + '\\n';"); 
    documentLines.add("         textToSave[2] = headerLine_2 + '\\n';"); 
    documentLines.add("         var taskRow = document.getElementsByTagName('TBODY').item(0).children;");     
    documentLines.add("         for(r=1; r<totalOrders+1; r++){"); 
    documentLines.add("             var textLine = taskRow[r-1].cells[2].innerText + ';';"); 
    documentLines.add("                 for(c=3; c<taskRow[r-1].cells.length; c++){"); 
    documentLines.add("                     if(c==taskRow[r-1].cells.length-1) textLine +=(taskRow[r-1].cells[c].innerText) + ';\\n'");
    documentLines.add("                 else textLine +=taskRow[r-1].cells[c].innerText + ';';");
    documentLines.add("                 };"); 
    documentLines.add("             textLine = textLine.replace(/\\./g,',');"); 
    documentLines.add("             textToSave[r+2] = textLine;"); 
    documentLines.add("         };"); 
    documentLines.add("         var footerLine = 'Podsumowanie:' + ';';");
    documentLines.add("         for(f=2; f<summary.length; f++){"); 
    documentLines.add("         if(summary[f].innerText == '✘') footerLine += 'x;' "); 
    documentLines.add("          else footerLine += summary[f].innerText + ';';"); 
    documentLines.add("         };"); 
    documentLines.add("         textToSave[totalOrders+3] = footerLine.replace(/\\./g,',');"); 
    documentLines.add("         createFile();"); 
    documentLines.add("     }"); 
    documentLines.add("\n");
    
    documentLines.add("  </script>");
    documentLines.add("</head>");
    
    //body
    documentLines.add("\n");
    documentLines.add("<body>");
    documentLines.add("<div id=\"header\">");
    documentLines.add("  <span style=\"font-size: 1.4em; \">Karta Czasu Pracy</span>");
    documentLines.add("  <span id=\"date\" style = \"float: right;\"></span>");
    documentLines.add("</div>");
    documentLines.add("  <div style=\"width: 100%\"><button style=\"float:left; background-color: blue; margin-bottom: 5px; margin-left: 5px\" class=\"button\" onclick=\"signIt()\">Pracownik</button><button style=\"float:right; background-color: blue; margin-bottom: 5px; margin-right: 5px\" class=\"button\" onclick=\"reset()\">Reset</button></div>");
    documentLines.add("  <span id=\"employee\" style=\"color:blue; margin-left: 5px; font-family: Tahoma; font-size: 0.7em; font-weight: bolder;\"></span>");
    
    //table
    documentLines.add("\n");
    documentLines.add("  <table id = \"tabela\" style=\"width:100%; box-shadow: 10px 10px 5px -4px rgba(179,179,179,1);\">");

        //thead
        
    documentLines.add("     <thead>");
    
            //Oznaczenie dni tygodnia
   
    documentLines.add("         <tr id=\"tableHeader\">");
    documentLines.add("             <th rowspan=\"3\" style=\"background-color: aliceblue; \">Lp.</br><button style=\"background-color: #008CBA; margin-top: 8px;\"  title=\"Dodaj zadanie\" class=\"button\" onclick=\"addTableRow("+ Arrays.toString(workDays) +")\">&#10010;</button></th>");
    documentLines.add("             <th colspan=\"2\" rowspan=\"3\" style=\"background-color: aliceblue; \">" + wtc.getNameOfMonth() + " " + date.get(Calendar.YEAR) +"<br>Liczba godz.: <span id=\"labourTotalTime\">"+ wtc.getLabourTotalTime() + "</span></th>");
    int j=0;    
    for(int i=0; i<weekDays.length;i++){
          while(j<freeDays.length){   
            if(freeDays[j]==i) { 
                documentLines.add("             <th style = \"color: orangered;\">"+weekDays[i]+"</th>"); 
                j++; break;
            } 
            else {
                documentLines.add("             <th>"+weekDays[i]+"</th>");
                break;
            }
         } 
    }
    documentLines.add("         </tr>");
    
            //Numery dni miesiąca
    
    documentLines.add("         <tr id=\"monthDays\">");
    int x=0;
           for(int i=0; i<monthDays.length; i++){
              while(x<freeDays.length){                
                    if(i == freeDays[x]){
                      documentLines.add("             <th style = \"color: orangered;  background-color: white;\">"+ monthDays[i] +"</th>");
                         x++; break;
                    } 
                    else{
                        documentLines.add("           <th style = \"background-color: white;\"><a href=\"javascript:makeAbsence(" + (i+1) + ");\"><span id=\"disabled_"+ i +"\">" + monthDays[i] + "</span></a></th>");
                        break;
                    }
               }
            }
           
    documentLines.add("         </tr>");
    
            //Dzienny Czas pracy
    
    documentLines.add("         <tr id =\"workDaysHours\">");
           for(int i=0; i<workDays.length;i++){
             if(workDays[i]==0){
                documentLines.add("             <th style = \"color: orangered;\">&#10008;</th>");
            }
             else{
                documentLines.add("             <th class=\"workDays\"><span id=\"workDay_"+ (i+1) +"\">"+ workDays[i] +"</span></th>");
               
            }
        }   
    documentLines.add("         </tr>");
    documentLines.add("     </thead>");
    
        //tbody
    
    documentLines.add("     <tbody>");
    if(data!=null){
        for(int k=0; k<data.length;k++){
            documentLines.add("         <tr id=\"row_id_"+ (k+1) +"\">");
            documentLines.add("             <td id=\"lp_id_"+ k +"\" class = \"closed\">"+ (k+1) +"</td>");
            documentLines.add("             <td style = \"border-right:none;\"><button style=\"background-color: red;\" title=\"Usuń zadanie\" class=\"button\" onclick=\"deleteTableRow(" + (k+1) + ")\">&#10006;</button></td>");
            documentLines.add("             <td style = \"border-left:none; text-align: left\" id=\"task_id_"+ k +"\" class = \"opened\"><a title=\"Numer zadania\" href=\"javascript:changeTaskNo('task_" + k + "', document.getElementById('task_"+ k +"').innerHTML);\"><span class=\"taskNo\" id=\"task_" +k+"\">"+data[k]+"</span></a></td>");
           int y=0;  
                for(int g=0; g<monthDays.length;g++){
                  
                  while(y<freeDays.length){ //Dni świąteczne               
                    if(g == freeDays[y]){
                      documentLines.add("             <td id=\"day_id_"+ g +"\" class = \"closed\"><span class=\"inactive\"></span></td>");
                         y++; break;
                    } 
                    else { //Dni robocze
                        if(workDays[g]>=unitTimePerHour && k<data.length-1){
                            documentLines.add("             <td id=\"day_id_"+ g +"\" class = \"opened row\"><a href=\"javascript:changeValue('" + data[k] + ", dn. " + (g+1) + "." + date.get(Calendar.MONTH)+1 + "." + date.get(Calendar.YEAR) + "', 'cell_" + (k+1) +"-"+(g+1)+ "');\">   <span class=\"active\" id=\"cell_"+(k+1)+"-"+(g+1)+"\" title=\""+ "Dzień: " +(g+1)+ "." + date.get(Calendar.MONTH)+1 + "." + date.get(Calendar.YEAR) + "\">"+unitTimePerHour+"</span></a></td>"); 
                            workDays[g] = Math.round((workDays[g] - unitTimePerHour)*10.0)/10.0;
                            break;
                            }
                        else 
                                if((workDays[g]<unitTimePerHour && workDays[g]>0) || (k==data.length-1)){ //Ostatni wiersz z zadaniem
                                        if(workDays[g] != 0) {
                                           documentLines.add("             <td id=\"day_id_"+ g +"\" class = \"opened row\"><a href=\"javascript:changeValue('" + data[k] + ", dn. "+(g+1)+ "." + date.get(Calendar.MONTH)+1 + "." + date.get(Calendar.YEAR) + "', 'cell_" + (k+1) +"-"+(g+1)+ "');\"><span class=\"active\" id=\"cell_"+(k+1)+"-"+(g+1)+"\" title=\""+ "Dzień: " +(g+1)+ "." + date.get(Calendar.MONTH)+1 + "." + date.get(Calendar.YEAR) + "\">"+ workDays[g] +"</span></a></td>");
                                           workDays[g] = 0;
                                        }
                                        else
                                           documentLines.add("             <td id=\"day_id_"+ g +"\" class = \"opened row\"><a href=\"javascript:changeValue('" + data[k] + ", dn. "+(g+1)+ "." + date.get(Calendar.MONTH)+1 + "." + date.get(Calendar.YEAR) + "', 'cell_" + (k+1) +"-"+(g+1)+ "');\"><span class=\"active\" id=\"cell_"+(k+1)+"-"+(g+1)+"\" title=\""+ "Dzień: " +(g+1)+ "." + date.get(Calendar.MONTH)+1 + "." + date.get(Calendar.YEAR) + "\">0.0</span></a></td>"); break;
                                        }
                                 else {
                                    documentLines.add("             <td id=\"day_id_"+ g +"\" class = \"opened row\"><a href=\"javascript:changeValue('" + data[k] + ", dn. "+(g+1)+ "." + date.get(Calendar.MONTH)+1 + "." + date.get(Calendar.YEAR) + "', 'cell_" + (k+1) +"-"+(g+1)+ "');\"><span class=\"active\" id=\"cell_"+(k+1)+"-"+(g+1)+"\" title=\""+ "Dzień: " +(g+1)+ "." + date.get(Calendar.MONTH)+1 + "." + date.get(Calendar.YEAR) + "\">"+ workDays[g] +"</span></a></td>"); break;
                                 } 
                    }
                }  
            }
            documentLines.add("         </tr>");
        }
    }  
        
    documentLines.add("         <tr>");
    documentLines.add("             <td class =\"foot\" style = \"font-weight: bold;\"><span class=\"inactive\" id=\"totalOrderNo\"></span></td>");
    documentLines.add("             <td colspan=\"2\" class =\"foot\" style = \"font-weight: bold;\">Rozliczenie</td>");
    int r=0;  
      for(int i=0; i<monthDays.length;i++){
          while(r<freeDays.length){                
                    if(i == freeDays[r]){
                      documentLines.add("             <td class =\"foot\" style = \"font-size: 0.8em;font-weight: bold; color: orangered; \">&#10008;</td>");
                         r++; break;
                    } 
                    else{
                        documentLines.add("             <td id=\"sum_"+(i+1)+"\" class=\"workDayTimeSum foot\">"+ Math.round(workDays[i] * 10.0) / 10.0 +"</td>");
                        break;
                    }
               }
        }   
    documentLines.add("         </tr>");
    documentLines.add("     </tbody>");
    documentLines.add("  </table>");
    documentLines.add("<div>");
    documentLines.add("  <div style=\"width: 100%\"><button style=\"float:right; background-color: blue; margin-top: 5px; margin-right: 5px\" class=\"button\" onclick=\"javascript:prepareDataToSave("+monthDays.length+")\">Zapisz</button></div>");
    documentLines.add("</div>");
    documentLines.add("<script>");
   
    documentLines.add(" (function() {");
    documentLines.add("     var totalOrderNoCells = document.querySelectorAll('.taskNo').length;");
    documentLines.add("     document.getElementById(\"totalOrderNo\").innerHTML = totalOrderNoCells;");
    documentLines.add("     monthWorkDays.length = 31; monthWorkDaySum = parseFloat(document.getElementById('labourTotalTime').innerHTML);");
    documentLines.add("     document.getElementById('date').innerHTML = 'Wrocław, dn. ' + new Date().toISOString().split('T')[0];");
    documentLines.add("  })();");
    documentLines.add("</script>");
    documentLines.add("</body>"); 
    documentLines.add("</html>");

    document = new String[documentLines.size()];
    document = documentLines.toArray(document);
    return document;
}

    protected boolean saveDocument(){
        String[] document = prepareDocument();
        return dataWR.writeDataToFile(document, getFilename());
    }

    protected String getFilename(){
        return "roboczogodziny " + (wtc.currentMonth + 1) + "-" + date.get(Calendar.YEAR) + ".htm";
    }
}