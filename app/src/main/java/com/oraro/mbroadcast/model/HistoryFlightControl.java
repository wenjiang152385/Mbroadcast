package com.oraro.mbroadcast.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/23 0023.
 */
public class HistoryFlightControl {
    public List<FlightInfoTemp> createHistoryData() {
//        viewHolder.numberText.setText(flightInfoTemp.getFlightNumber());
//        viewHolder.statusText.setText(flightInfoTemp.getImportAndExport());
//        viewHolder.departText.setText(flightInfoTemp.getDepartureStation());
//        viewHolder.arriveText.setText(R.string.listview_text_title_delay_information_string);
//        viewHolder.delayText.setText(flightInfoTemp.getDestinationStation());
//        viewHolder.timeText.setText(flightInfoTemp.getPlanToTakeOff());
//        viewHolder.gateText.setText(flightInfoTemp.getBoardingGate());
        List<FlightInfoTemp> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            FlightInfoTemp historyFlightTempEdit = new FlightInfoTemp();
            historyFlightTempEdit.setFlightNumber(i + "000");
//            historyFlightTempEdit.setImportAndExport(i + "001");
            historyFlightTempEdit.setDeparture(i + "002");
            historyFlightTempEdit.setArrivalStation(i + "003");
            //historyFlightTempEdit.setPlanToTakeOff(i + "004");
            historyFlightTempEdit.setBoardingGate(i + "005");

            list.add(historyFlightTempEdit);
        }

        return list;
    }
}
