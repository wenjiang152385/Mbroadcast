/**
 * Created by Administrator on 2016/10/8 0008.
 */
//一个参数航班号
function valuationoneparameter(flightno){
    document.getElementById("flightno").innerHTML=flightno;
}

//两个参数航班号与目的地
function valuation(flightno,destination){
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
}
//三个参数航班号、始发地、目的地
function valuationthreeparameter(flightno,provenance,destination){
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
}


//航空公司
//1个参数航空公司、航班号
function ValuationOneParameterAboutAirlines(airlines){
    document.getElementById("airlines").innerHTML=airlines;
}
//两个参数航空公司、航班号
function Valuation(airlines,flightno){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
}
//三个参数航空公司、航班号、目的地
function ValuationThreeParameter(airlines,flightno,destination){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
}

//四个参数航空公司、航班号、始发地、目的地
function ValuationFourParameter(airlines,flightno,provenance,destination){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
}

//批量延误模板加载调用方法
function add(flightno){
    var span = document.createElement("span");
    span.setAttribute("id", flightno);//给新建的span标签设置id
    span.innerHTML ="  "+flightno;
    var deleteImg = document.createElement("img");
    deleteImg.setAttribute("src", "delete.png");
    deleteImg.onclick= function () {
        $("#"+flightno).remove();
        $(this).remove();
        window.stub.removeFlightnoId(flightno);
    };
    $("#flightno").append(span);
    $("#flightno").append(deleteImg);
}


// 1.1 寻找未登机乘客广播赋值函数（共享航班）
function LookingforBoardingPassengers_valuation(airlines,flightno,sharingflight,provenance,destination,name,seatnumber,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("name").value=name;
    document.getElementById("seatnumber").value=seatnumber;
    document.getElementById("counter").value=counter;
}

// 1.1 寻找未登机乘客广播
function LookingforBoardingPassengers(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var provenance = document.getElementById("provenance").textContent;
    var destination = document.getElementById("destination").textContent;
    var name = document.getElementById("name").value;
    var counter = document.getElementById("counter").value;
    var sharingflight = document.getElementById("sharingflight").value;
    var seatnumber = document.getElementById("seatnumber").value;
    var msg;
    if(name==''|| counter==''){
        msg = "信息填写不全";
    }else{
        if(sharingflight==''&& seatnumber==''){
            msg = '女士们，先生们，请注意: '+message1+message2+name+' '+'速由第 '+counter+' 号登机口登机，谢谢！';
        }else if(sharingflight!=''&& seatnumber==''){
            msg = '女士们，先生们，请注意: '+message1+'(代码共享航班 '+sharingflight+')'+message2+name+' '+'速由第 '+counter+' 号登机口登机，谢谢！';
        }else if(sharingflight==''&& seatnumber!=''){
            msg = '女士们，先生们，请注意: '+message1+message2+name+' '+'（座位号为 '+seatnumber+' 的旅客）'+'速由第 '+counter+' 号登机口登机，谢谢！';
        }else{
            msg = '女士们，先生们，请注意: '+message1+'(代码共享航班 '+sharingflight+')'+message2+name+' '+'（座位号为 '+seatnumber+' 的旅客）'+'速由第 '+counter+' 号登机口登机，谢谢！';

        }
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight + '-' + provenance + '-' + destination + '-'+ name + '-' + seatnumber + '-' + counter);
    window.stub.jsMethod(msg);
}


// 1.2 寻找旅客广播赋值函数（共享航班）
function LookingforPassengers_valuation(airlines,flightno,sharingflight,provenance,destination,name,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("name").value=name;
    document.getElementById("counter").value=counter;
}

// 1.2 寻找旅客广播
function LookingforPassengers(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var provenance = document.getElementById("provenance").textContent;
    var destination = document.getElementById("destination").textContent;
    var name = document.getElementById("name").value;
    var counter = document.getElementById("counter").value;
    var sharingflight = document.getElementById("sharingflight").value;
    var msg;
    if(name==''|| counter==''){
        msg = "信息填写不全";
    }else{
        if(sharingflight==''){
            msg = '女士们，先生们，请注意: '+message1+message2+name+' '+'与 '+counter+' 号登机口工作人员联系，谢谢!';
        }else{
            msg = '女士们，先生们，请注意: '+message1+'(代码共享航班 '+sharingflight+')'+message2+name+' '+'与 '+counter+' 号登机口工作人员联系，谢谢!';
        }
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight + '-' + provenance + '-' + destination + '-'+ name  + '-' + counter);
    window.stub.jsMethod(msg);
}


// 1.3 航班延误广播赋值函数
function FlightDelays_valuation(airlines,flightno,sharingflight,provenance,destination,radiovalue){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
    var radio = document.getElementsByName("delayreason");
    for(var i=0;i<radio.length;i++){
        if(radio[i].value==radiovalue){
            radio[i].setAttribute("checked",true);
            break;
        }
    }
}

// 1.3 航班延误广播
function FlightDelays(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var provenance = document.getElementById("provenance").textContent;
    var destination = document.getElementById("destination").textContent;
    var value = '';
    var radio = document.getElementsByName("delayreason");
    var sharingflight = document.getElementById("sharingflight").value;
    for(var i=0;i<radio.length;i++){
        if(radio[i].checked==true){
            value=radio[i].value;
            break;
        }
    }
    var msg;
    if(value==''){
        msg = "信息填写不全";
    }else{
        if(sharingflight==''){
            msg = '女士们，先生们，请注意: '+message1+message2+value+' 的原因，本次航班将延迟登机，请在大厅内耐心等候，登机时间我们将稍后广播通知。如您需要任何协助，请与登机口工作人员联系，感谢您的合作与谅解。';

        }else{
            msg = '女士们，先生们，请注意: '+message1+'(代码共享航班 '+sharingflight+')'+message2+value+' 的原因，本次航班将延迟登机，请在大厅内耐心等候，登机时间我们将稍后广播通知。如您需要任何协助，请与登机口工作人员联系，感谢您的合作与谅解。';
        }
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight +'-' + provenance + '-' + destination + '-' + value);
    window.stub.jsMethod(msg);
}
// 1.4 批量延误赋值函数（共享航班，延误原因）
//function BulkDelayBroadcasting_valuation(flightno,sharingflight,radiovalue){
//    document.getElementById("flightno").innerHTML=flightno;
//    document.getElementById("sharingflight").value=sharingflight;
//    var radio = document.getElementsByName("delayreason");
//    for(var i=0;i<radio.length;i++){
//        if(radio[i].value==radiovalue){
//            radio[i].setAttribute("checked",true);
//            break;
//        }
//    }
//}
//批量延误赋值函数（共享航班，延误原因）
function BulkDelayBroadcasting_valuation(flightno,sharingflight,radiovalue){
    document.getElementById("sharingflight").value=sharingflight;
    var arr = flightno.split("  ");
    for(var i=1;i<arr.length;i++){
        add(arr[i]);
    }
    var radio = document.getElementsByName("delayreason");
    for(var i=0;i<radio.length;i++){
        if(radio[i].value==radiovalue){
            radio[i].setAttribute("checked",true);
            break;
        }
    }
}
// 1.4 批量延误******************
   function BulkDelayBroadcasting(){
       var message1 = document.getElementById("message1").textContent;
       var flightno = document.getElementById("flightno").textContent;
       var a = message1.split("  ");
       var b = a.slice(1);
       var message = b.join("、");
       var message2 = document.getElementById("message2").textContent;
       var value = '';
       var radio = document.getElementsByName("delayreason");
       var sharingflight = document.getElementById("sharingflight").value;
       for(var i=0;i<radio.length;i++){
           if(radio[i].checked==true){
               value=radio[i].value;
               break;
           }
       }
       var msg;
       if(value==''){
           msg = "信息填写不全";
       }else{
           if(sharingflight==''){
               msg = '女士们，先生们，请注意: '+message+message2+value+'的原因，本次航班将延迟登机，请在大厅内耐心等候，登机时间我们将稍后广播通知。如您需要任何协助，请与登机口工作人员联系，感谢您的合作与谅解。';

           }else{
               msg = '女士们，先生们，请注意: '+message+'(代码共享航班 '+sharingflight+')'+message2+value+' 的原因，本次航班将延迟登机，请在大厅内耐心等候，登机时间我们将稍后广播通知。如您需要任何协助，请与登机口工作人员联系，感谢您的合作与谅解。';
           }
       }
       if(document.getElementById("flightno").textContent != ""){

       }else{
            msg="航班号不能为空";
       }
       window.stub.getJsParams(flightno + '-' +sharingflight + '-' + value);
       window.stub.jsMethod(msg);
   }
// 1.5 登机通道拥堵广播赋值函数（共享航班）
function BoardingChannelCongestion_valuation(airlines,flightno,sharingflight,provenance,destination){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("sharingflight").value=sharingflight;
}
// 1.5 登机通道拥堵广播（适用于同一登机口同时上下客）
function BoardingChannelCongestion(){
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var provenance = document.getElementById("provenance").textContent;
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var sharingflight = document.getElementById("sharingflight").value;
    var msg;
    if(sharingflight==''){
        msg = '女士们，先生们，请注意: '+message1+message2;

    }else{
        msg = '女士们，先生们，请注意: '+message1+'(代码共享航班 '+sharingflight+')'+message2;
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight +'-' + provenance + '-' + destination);
    window.stub.jsMethod(msg);
}


// 1.6 登机口更改广播赋值函数（共享航班）
function GateChange_valuation(airlines,flightno,sharingflight,provenance,destination,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("counter").value=counter;
}

// 1.6 登机口更改广播
function GateChange(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var provenance = document.getElementById("provenance").textContent;
    var counter = document.getElementById("counter").value;
    var sharingflight = document.getElementById("sharingflight").value;
    var msg;
    if(counter==''){
        msg = "信息填写不全";
    }else{
        if(sharingflight==''){
            msg = '女士们，先生们，请注意: '+message1+message2+counter+' 号登机口候机，谢谢！';

        }else{
            msg = '女士们，先生们，请注意: '+message1+'(代码共享航班 '+sharingflight+')'+message2+counter+' 号登机口候机，谢谢！';
        }
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight +'-' + provenance + '-' + destination + '-' + counter);
    window.stub.jsMethod(msg);
}


// 1.7 过站航班登机广播赋值函数（共享航班）
function StandingBoardingFlight_valuation(color,airlines,flightno,sharingflight,destination,counter){
    document.getElementById("color").value=color;
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("counter").value=counter;
}

// 1.7 过站航班登机广播
function StandingBoardingFlight(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var counter = document.getElementById("counter").value;
    var color = document.getElementById("color").value;
    var sharingflight = document.getElementById("sharingflight").value;
    var msg;
    if(counter==''||color==''){
        msg = "信息填写不全";
    }else{
        if(sharingflight==''){
            msg = '手持 '+color+' 颜色过站登机牌的旅客请注意：'+message1+message2+counter+' 号门登机。祝各位旅客旅途愉快，谢谢！';

        }else{
            msg = '手持 '+color+' 颜色过站登机牌的旅客请注意：'+message1+'(代码共享航班 '+sharingflight+')'+message2+counter+' 号门登机。祝各位旅客旅途愉快，谢谢！';
        }
    }
    window.stub.getJsParams(color + '-' + airlines + '-' + flightno + '-' + sharingflight + '-' + destination + '-' + counter);
    window.stub.jsMethod(msg);
}


//  1.8 航班延误/补班安排休息广播 赋值函数（经停，延误原因，单选项）
function FlightfillingClassArranged_valuation(airlines,flightno,destination,stopping,delay,radiovalue){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("stopping").value=stopping;
    document.getElementById("delay").value=delay;
    var radio = document.getElementsByName("delay/cancel");
    for(var i=0;i<radio.length;i++){
        if(radio[i].value==radiovalue){
            radio[i].setAttribute("checked",true);
            break;
        }
    }
}
// 1.8 航班延误/补班安排休息广播
function FlightfillingClassArranged(){
    var message = document.getElementById("message").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var stopping = document.getElementById("stopping").value;
    var delay = document.getElementById("delay").value;
    var value = '';
    var radio = document.getElementsByName("delay/cancel");
    for(var i=0;i<radio.length;i++){
        if(radio[i].checked==true){
            value=radio[i].value;
            break;
        }
    }
    var msg;
    if(delay==''){
        msg = "信息填写不全";
    }else{
        if(stopping==''){
            msg = '女士们，先生们：'+message+'的旅客请注意：本航班由于 '+delay+' 原因'+value+'. 在此向各位表示歉意，请旅客们带好随身物品，到登机口与工作人员联系，我们将安排各位前往宾馆休息，谢谢合作！';

        }else{
            msg = '女士们，先生们：'+message+'(经停 '+stopping+' ) 的旅客请注意: 本航班由于 '+delay+' 原因'+value+'. 在此向各位表示歉意，请旅客们带好随身物品，到登机口与工作人员联系，我们将安排各位前往宾馆休息，谢谢合作！';

        }
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + destination +'-' + stopping + '-' + delay + '-' + value);
    window.stub.jsMethod(msg);
}

// 1.9 航班取消/补班安排餐饮广播赋值函数（共享航班）
function FlightCancelledfillingArrangements_valuation(airlines,flightno,provenance,destination,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("counter").value=counter;
}
// 1.9 航班取消/补班安排餐饮广播
function FlightCancelledfillingArrangements(){
    var message = document.getElementById("message").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var provenance = document.getElementById("provenance").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var counter = document.getElementById("counter").value;
    var msg;
    if(counter==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们: '+message+' '+counter+' 登机口凭登机牌领取饮料/餐食。';
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + provenance + '-' + destination +'-' + counter);
    window.stub.jsMethod(msg);
}


// 2.1 柜台关闭广播赋值函数（共享航班）
function ShutdownCounter_valuation(airlines,flightno,sharingflight,provenance,destination,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("counter").value=counter;
}

// 2.1 柜台关闭广播
function ShutdownCounter(){
    var counter = document.getElementById("counter").value;
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var provenance = document.getElementById("provenance").textContent;
    var sharingflight = document.getElementById("sharingflight").value;
    var msg;
    if(counter==''){
        msg = "信息填写不全";
    }else{
        if(sharingflight==''){
            msg = '女士们，先生们，请注意: '+message1+message2+' '+counter+' 号柜台，谢谢。';

        }else{
            msg = '女士们，先生们，请注意: '+message1+'(代码共享航班 '+sharingflight+')'+message2+' '+counter+' 号柜台，谢谢。';
        }
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight + '-' + provenance + '-' + destination +'-' + counter);
    window.stub.jsMethod(msg);
}


// 2.2 呼叫旅客赋值函数（人员姓名，柜台号，人员称呼）
function CallPassenger_valuation(flightno,destination,name,counter,radiovalue){
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("name").value=name;
    document.getElementById("counter").value=counter;
    var radio = document.getElementsByName("identity");
    for(var i=0;i<radio.length;i++){
        if(radio[i].value==radiovalue){
            radio[i].setAttribute("checked",true);
            break;
        }
    }
}
// 2.2 呼叫旅客
function CallPassenger(){
    var message = document.getElementById("message").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var name = document.getElementById("name").value;
    var counter = document.getElementById("counter").value;
    var value = '';
    var radio = document.getElementsByName("identity");
    for(var i=0;i<radio.length;i++){
        if(radio[i].checked==true){
            value=radio[i].value;
            break;
        }
    }
    var msg;
    if(name==''|| counter==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们：'+message+name+' '+value+'请注意。请您听到广播后，前往 '+counter+' 号办票柜台，谢谢。';
    }
    window.stub.getJsParams(flightno + '-' +destination +'-' + name + '-' + counter +'-' + value);
    window.stub.jsMethod(msg);
}


// 2.3 呼叫行李开检旅客赋值函数（人员姓名，柜台号，人员称呼）
function CallPassengersCheckin_valuation(flightno,name,counter,radiovalue){
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("name").value=name;
    document.getElementById("counter").value=counter;
    var radio = document.getElementsByName("identity");
    for(var i=0;i<radio.length;i++){
        if(radio[i].value==radiovalue){
            radio[i].setAttribute("checked",true);
            break;
        }
    }
}
// 2.3 呼叫行李开检旅客
function CallPassengersCheckin(){
    var message = document.getElementById("message").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var name = document.getElementById("name").value;
    var counter = document.getElementById("counter").value;
    var value = '';
    var radio = document.getElementsByName("identity");
    for(var i=0;i<radio.length;i++){
        if(radio[i].checked==true){
            value=radio[i].value;
            break;
        }
    }
    var msg;
    if(name==''|| counter==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们：'+message+name+' '+value+'前往 '+counter+' 号柜台, 协助行李检查。谢谢您的合作！';
    }
    window.stub.getJsParams(flightno + '-' + name + '-' + counter +'-' + value);
    window.stub.jsMethod(msg);
}

// 2.4 呼叫候补旅客赋值函数（共享航班，柜台号）
function CalltheWaiting_valuation(airlines,flightno,sharingflight,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("counter").value=counter;
}
// 2.4 呼叫候补旅客
function CalltheWaiting(){
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var sharingflight = document.getElementById("sharingflight").value;
    var counter = document.getElementById("counter").value;
    var msg;
    if(counter==''){
        msg = "信息填写不全";
    }else{
        if(sharingflight==''){
            msg = '女士们，先生们: '+message1+message2+counter+' 号办票柜台，谢谢。';

        }else{
            msg = '女士们，先生们: '+message1+'(代码共享航班 '+sharingflight+')'+message2+' '+counter+' 号办票柜台，谢谢。';
        }
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight + '-' + counter);
    window.stub.jsMethod(msg);
}


// 2.5 天气原因航班延误赋值函数（共享航班）
function WeatherDelays_valuation(airlines,flightno,sharingflight,destination){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("destination").innerHTML=destination;
}
// 2.5 天气原因航班延误
function WeatherDelays(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var sharingflight = document.getElementById("sharingflight").value;
    var msg;
    if(sharingflight==''){
        msg = '女士们，先生们: '+message1+message2;

    }else{
        msg = '女士们，先生们: '+message1+'(代码共享航班 '+sharingflight+')'+message2;
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight + '-' + destination);
    window.stub.jsMethod(msg);
}


// 2.6 机故原因赋值函数（共享航班）
function MachineReason_valuation(airlines,flightno,sharingflight,destination){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("destination").innerHTML=destination;
}

// 2.6 机故原因
function MachineReason(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var sharingflight = document.getElementById("sharingflight").value;
    var msg;
    if(sharingflight==''){
        msg = '女士们，先生们: '+message1+message2;

    }else{
        msg = '女士们，先生们: '+message1+'(代码共享航班 '+sharingflight+')'+message2;
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight + '-' + destination);
    window.stub.jsMethod(msg);
}

// 2.7 电脑系统故障
function ComputerSystemFailure(){
    var message = document.getElementById("message").textContent;
    var msg;
    msg = message;
    window.stub.getJsParams("无参数");
    window.stub.jsMethod(msg);
}

// 2.8 航班取消赋值函数（共享航班，延误原因）
function FlightCancelled_valuation(airlines,flightno,sharingflight,destination,cancel,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("sharingflight").value=sharingflight;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("cancel").value=cancel;
    document.getElementById("counter").value=counter;
}
// 2.8 航班取消
function FlightCancelled(){
    var counter = document.getElementById("counter").value;
    var cancel = document.getElementById("cancel").value;
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var sharingflight = document.getElementById("sharingflight").value;
    var msg;
    if(counter==''){
        msg = "信息填写不全";
    }else{
        if(sharingflight==''){
            msg = '女士们，先生们: '+message1+message2+cancel+'原因已经被取消。请各位旅客前往'+counter+' 号柜台与我们工作人员联系，了解有关安排，谢谢。';

        }else{
            msg = '女士们，先生们: '+message1+'(代码共享航班 '+sharingflight+')'+message2+cancel+'原因已经被取消。请各位旅客前往'+counter+' 号柜台与我们工作人员联系，了解有关安排，谢谢。';
        }
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + sharingflight + '-' + destination + '- ' + cancel + '-' + counter);
    window.stub.jsMethod(msg);
}

// 2.9 严禁替他人托运行李赋值函数
function CheckedForbiddenOthers_valuation(airlines){
    document.getElementById("airlines").innerHTML=airlines;
}
// 2.9 严禁替他人托运行李
function CheckedForbiddenOthers(){
    var message = document.getElementById("message").textContent;
    var msg;
    msg = '女士们,先生们，请注意: '+message;
    window.stub.getJsParams(message);
    window.stub.jsMethod(msg);
}


// 2.10 失物招领赋值函数（共享航班）
function TheLostAndFound_valuation(lostplace,lostitems,foundplace){
    document.getElementById("lostplace").value=lostplace;
    document.getElementById("lostitems").value=lostitems;
    document.getElementById("foundplace").value=foundplace;
}
// 2.10 失物招领
function TheLostAndFound(){
    var message = document.getElementById("message").textContent;
    var lostplace = document.getElementById("lostplace").value;
    var lostitems = document.getElementById("lostitems").value;
    var foundplace = document.getElementById("foundplace").value;
    var msg;
    if(lostplace==''||lostitems==''||foundplace==''){
        msg = "信息填写不全";
    }else{
        msg = message+lostplace+' 处丢失了 '+lostitems+' ，请听到广播后，前往 '+foundplace+' 认领您的遗失物品，谢谢。';
    }
    window.stub.getJsParams(lostplace + '-' + lostitems + '-' + foundplace);
    window.stub.jsMethod(msg);
}


// 3.1.1 东航乘客/最后广播赋值函数（原登机口，现登机口）
function LastBroadcast_valuation(airlines,flightno,destination,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("counter").value=counter;
}
// 3.1.1 东航乘客/最后广播
function LastBroadcast(){
    var message = document.getElementById("message").textContent;
    var counter = document.getElementById("counter").value;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var msg;
    if(counter==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们: '+message+counter+' 号登机口登机，谢谢。';
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + destination + '-' + counter);
    window.stub.jsMethod(msg);
}


// 3.1.2 东航乘客/登机口更改赋值函数（原登机口，现登机口）
function ChinaEasternGateChange_valuation(airlines,flightno,destination,counter,counter1){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("counter").value=counter;
    document.getElementById("counter1").value=counter1;
}

// 3.1.2 东航乘客/登机口更改
function ChinaEasternGateChange(){
    var message = document.getElementById("message").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var counter = document.getElementById("counter").value;
    var counter1 = document.getElementById("counter1").value;
    var msg;
    if(counter==''||counter1==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们: '+message+counter+' 号更改为'+counter1+' 号，谢谢。';
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' +destination + '-' + counter + '-' +counter1);
    window.stub.jsMethod(msg);
}


// 3.1.3 东航乘客/延误广播赋值函数（xx机场，延误原因，时间更改为xx）
function DelayBroadcasting_valuation(airlines,flightno,destination,radiovalue,counter,hour,second){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    var radio = document.getElementsByName("dhdelayreason");
    for(var i=0;i<radio.length;i++){
        if(radio[i].value==radiovalue){
            radio[i].setAttribute("checked",true);
            if(radiovalue=="机场天气条件"){
                document.getElementById("counter").value=counter;
            }else if(radiovalue=="登机时间更改为"){
                document.getElementById("hour").value=hour;
                document.getElementById("second").value=second;
            }
        }
    }
}
// 3.1.3 东航乘客/延误广播
function DelayBroadcasting(){
    var message = document.getElementById("message").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var counter = document.getElementById("counter").value;
    var obj1 = document.getElementById("hour"); //定位id
    var obj2 = document.getElementById("second"); //定位id
    var index1 = obj1.selectedIndex; // 选中索引
    var value1 = obj1.options[index1].value; // 选中值
    var index2 = obj2.selectedIndex; // 选中索引
    var value2 = obj2.options[index2].value; // 选中值
    var value = '';
    var radio = document.getElementsByName("dhdelayreason");
    for(var i=0;i<radio.length;i++){
        if(radio[i].checked==true){
            var value3=radio[i].value;
            if(value=='机场天气条件'){
                value=counter+'机场天气条件';
            }else if(value=='登机时间更改为'){
                value='登机时间更改为 '+value1+':'+value2;
            }else{
                value=radio[i].value;
            }
            break;
        }
    }
    var msg;
    if(value==''){
        msg = "信息填写不全";
    }else if(value=="机场天气条件"&&counter==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们: '+message+value+' 的原因，您乘坐的航班不能按时起飞。请您继续在休息室等候登机，谢谢您的合作。';
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' +destination + '-' + value3 + '-' +counter + '-' + value1 + '-' + value2);
    window.stub.jsMethod(msg);
}


// 3.1.4 东航/寻找旅客广播赋值函数（人员姓名）
function ChinaEasterLookingforPassengers_valuation(airlines,flightno,destination,name){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("name").value=name;
}
// 3.1.4 东航/寻找旅客广播
function ChinaEasterLookingforPassengers(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var name = document.getElementById("name").value;
    var msg;
    if(name==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们，请注意: '+message1+message2+name+' '+'先生/女士'+' ，请与贵宾室接待柜台联系，谢谢。';
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' +destination + '-' + name);
    window.stub.jsMethod(msg);
}


// 3.1.5 东航乘客/失物招领广播赋值函数（物品丢失区域）
function ChinaEasternTheLostAndFound_valuation(radiovalue){
    var radio = document.getElementsByName("lost");
    for(var i=0;i<radio.length;i++){
        if(radio[i].value==radiovalue){
            radio[i].setAttribute("checked",true);
            break;
        }
    }
}
// 3.1.5 东航乘客/失物招领广播
function ChinaEasternTheLostAndFound(){
    var message = document.getElementById("message").textContent;
    var value = '';
    var radio = document.getElementsByName("lost");
    for(var i=0;i<radio.length;i++){
        if(radio[i].checked==true){
            value=radio[i].value;
            break;
        }
    }
    var msg;
    if(value==''){
        msg = "信息填写不全";
    }else{
        msg = message+value+' 区域遗失了物品，请马上到接待柜台与工作人员联系，谢谢。';
    }
    window.stub.getJsParams(value);
    window.stub.jsMethod(msg);
}

// 3.2.1 非东航乘客/登机口更改赋值函数（原登机口，现登机口）
function NotChinaEasternGateChange_valuation(airlines,flightno,destination,counter,counter1){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("counter").value=counter;
    document.getElementById("counter1").value=counter1;
}
// 3.2.1 非东航乘客/登机口更改
function NotChinaEasternGateChange(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var counter = document.getElementById("counter").value;
    var counter1 = document.getElementById("counter1").value;
    var msg;
    if(counter==''||counter1==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们: '+message1+message2+counter+' 号更改为'+counter1+' 号，谢谢。';

    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' +destination + '-' + counter + '-' +counter1);
    window.stub.jsMethod(msg);
}


// 3.2.2 非东航乘客/延误广播赋值函数（xx机场，延误原因，时间更改为xx）
function NotChinaEasternDelayBroadcasting_valuation(airlines,flightno,destination,radiovalue,counter,hour,second){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    var radio = document.getElementsByName("dhdelayreason");
    for(var i=0;i<radio.length;i++){
        if(radio[i].value==radiovalue){
            radio[i].setAttribute("checked",true);
            if(radiovalue=="机场天气条件"){
                document.getElementById("counter").value=counter;
            }else if(radiovalue=="登机时间更改为"){
                document.getElementById("hour").value=hour;
                document.getElementById("second").value=second;
            }
        }
    }
}
// 3.2.2 非东航乘客/延误广播
function NotChinaEasternDelayBroadcasting(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var counter = document.getElementById("counter").value;
    var obj1 = document.getElementById("hour"); //定位id
    var obj2 = document.getElementById("second"); //定位id
    var index1 = obj1.selectedIndex; // 选中索引
    var value1 = obj1.options[index1].value; // 选中值
    var index2 = obj2.selectedIndex; // 选中索引
    var value2 = obj2.options[index2].value; // 选中值
    var value = '';
    var radio = document.getElementsByName("dhdelayreason");
    for(var i=0;i<radio.length;i++){
        if(radio[i].checked==true){
            var value3=radio[i].value;
            if(value=='机场天气条件'){
                value=counter+'机场天气条件';
            }else if(value=='登机时间更改为'){
                value='登机时间更改为 '+value1+':'+value2;
            }else{
                value=radio[i].value;
            }
            break;
        }
    }
    var msg;
    if(value==''){
        msg = "信息填写不全";
    }else if(value=="机场天气条件"&&counter==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们: '+message1+message2+value+' 的原因，您乘坐的航班不能按时起飞。请您继续在休息室等候登机，谢谢您的合作。';
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' +destination + '-' + value3 + '-' +counter + '-' + value1 + '-' + value2);
    window.stub.jsMethod(msg);
}

// 3.2.3 非东航/寻找旅客广播赋值函数（共享航班）
function NotChinaEasterLookingforPassengers_valuation(airlines,flightno,destination,name){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("name").value=name;
}
// 3.2.3 非东航/寻找旅客广播
function NotChinaEasterLookingforPassengers(){
    var message1 = document.getElementById("message1").textContent;
    var message2 = document.getElementById("message2").textContent;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var destination = document.getElementById("destination").textContent;
    var name = document.getElementById("name").value;
    var msg;
    if(name==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们，请注意: '+message1+message2+name+' '+'先生/女士'+' ，请与贵宾室接待柜台联系，谢谢。';
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + destination + '-' + name);
    window.stub.jsMethod(msg);
}


// 4.1 行李传送转盘变更广播赋值函数（共享航班）
function LuggageWheelChanges_valuation(airlines,flightno,provenance,destination,counter){
    document.getElementById("airlines").innerHTML=airlines;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("provenance").innerHTML=provenance;
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("counter").value=counter;
}
// 4.1 行李传送转盘变更广播
function LuggageWheelChanges(){
    var message = document.getElementById("message").textContent;
    var counter = document.getElementById("counter").value;
    var airlines = document.getElementById("airlines").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var provenance = document.getElementById("provenance").textContent;
    var destination = document.getElementById("destination").textContent;
    var msg;
    if(counter==''){
        msg = "信息填写不全";
    }else{
        msg = '女士们，先生们，请注意: '+message+' '+counter+' 号行李传送转盘提取行李。';
    }
    window.stub.getJsParams(airlines + '-' + flightno + '-' + provenance + '-' + destination + '-' + counter);
    window.stub.jsMethod(msg);
}

// 编辑页面：值机延误（有时间）
// E-1-6 航班延误/有时间赋值函数（xx机场，延误原因，时间更改为xx）
function DelayTime_valuation(destination,flightno,hour,second){
    document.getElementById("destination").innerHTML=destination;
    document.getElementById("flightno").innerHTML=flightno;
    document.getElementById("hour").value=hour;
    document.getElementById("second").value=second;

}
// E-1-6 航班延误/有时间
function DelayTime(){
    var message = document.getElementById("message").textContent;
    var destination = document.getElementById("destination").textContent;
    var flightno = document.getElementById("flightno").textContent;
    var obj1 = document.getElementById("hour"); //定位id
    var obj2 = document.getElementById("second"); //定位id
    var index1 = obj1.selectedIndex; // 选中索引
    var value1 = obj1.options[index1].value; // 选中值
    var index2 = obj2.selectedIndex; // 选中索引
    var value2 = obj2.options[index2].value; // 选中值
    var msg;
    msg =message+value1+":"+value2+' 。请您在出发厅休息，等候通知。谢谢！';
    window.stub.getJsParams(destination + '-' + flightno + '-' + value1 + '-' +value2);
    window.stub.jsMethod(msg);
}


// 切换html文件是否可编辑
function changeStyle(event) {
//document.getElementById("airlines").innerHTML="--->"+event;
        if(event == "1") {
            //不可编辑
//document.getElementById("airlines").innerHTML="--->"+event+"--->";

            var div1 = document.getElementById('usual');
            var div2 = document.getElementById('emergency');
            div1.style.display="none";
            div2.style.display="block";
            $("input").attr("disabled", true);
        }else if(event == "2"){
            //隐藏emergency,显示usual
            var div1 = document.getElementById('usual');
            var div2 = document.getElementById('emergency');
            div1.style.display="block";
            div2.style.display="none";
            $("input").attr("disabled", false);
        }
    }


window.onload=function(){
    //要初始化的东西
    $(function(){
        $("#usual").css("display","none");
        $("#emergency").css("display","block");
        $("input").attr("disabled", true);
    });

};
