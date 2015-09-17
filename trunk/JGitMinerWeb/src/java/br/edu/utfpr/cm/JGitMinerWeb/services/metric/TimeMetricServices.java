/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.metric;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.matriz.EntityMatriz;
import br.edu.utfpr.cm.JGitMinerWeb.model.matriz.EntityMatrizNode;
import br.edu.utfpr.cm.JGitMinerWeb.services.matriz.CoChangedFileServices;
import br.edu.utfpr.cm.JGitMinerWeb.services.matriz.auxiliary.AuxPairOfFiles;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.AuxCountCoChange;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.AuxTime;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.AuxTimeFinal;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.DayOfMonthCount;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.DayOfWeekCount;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.MonthCount;
import br.edu.utfpr.cm.JGitMinerWeb.services.metric.auxiliary.PeriodOfDayCount;
import br.edu.utfpr.cm.JGitMinerWeb.util.JsfUtil;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author geazzy
 */
public class TimeMetricServices extends AbstractMetricServices {

    List<AuxTime> auxTimeList;
    Map<AuxPairOfFiles, AuxCountCoChange> coChangeCountMap;

    public TimeMetricServices(GenericDao dao, EntityMatriz matriz, Map params, OutLog out) {
        super(dao, matriz, params, out);
        auxTimeList = new ArrayList<>();
        coChangeCountMap = new HashMap<>();
    }

    public TimeMetricServices(GenericDao dao, OutLog out) {
        super(dao, out);
        auxTimeList = new ArrayList<>();
        coChangeCountMap = new HashMap<>();
    }

    @Override
    public void run() {

        if (getMatriz() == null && !getAvailableMatricesPermitted()
                .contains(getMatriz().getClassServicesName())) {
            throw new IllegalArgumentException("Selecione uma matriz gerada pelo Service: "
                    + getAvailableMatricesPermitted());
        }

        System.out.println("Selecionado matriz com " + getMatriz().getNodes().size() + " nodes.");

        setAuxCoChangedList();
//        
//        for (AuxTime auxcochange : auxTimeList) {
//            //System.out.println(auxcochange.getAuxPairOfFiles().getArq1()+"---"+auxcochange.getAuxPairOfFiles().getArq2()+"---"+
//             //       auxcochange.getAuxPairOfFiles().getshaCommitFile2()+"---"+auxcochange.getAuxPairOfFiles().getshaCommitFile2());
//            
//            for (int j = 0; j < auxTimeList.size(); j++) {
//                    if (auxTimeList.get(j).getAuxPairOfFiles().equals(auxcochange.getAuxPairOfFiles())) {
//                        
//                         System.out.println(auxTimeList.get(j).getAuxPairOfFiles().getArq1()+"---"+auxTimeList.get(j).getAuxPairOfFiles().getArq2()+"---"+
//                    auxTimeList.get(j).getAuxPairOfFiles().getshaCommitFile2()+"---"+auxTimeList.get(j).getAuxPairOfFiles().getshaCommitFile2());
//                    }
//            }
//        }

        countCoChanges();

        List<AuxTimeFinal> countList = setCountList();

        addToEntityMetricNodeList(countList);

    }

    private void setAuxCoChangedList() throws NumberFormatException {

        for (EntityMatrizNode node : getMatriz().getNodes()) {
            String[] coluns = node.getLine().split(JsfUtil.TOKEN_SEPARATOR);
            //  System.out.println("linha: "+node.getLine());
            AuxPairOfFiles acc = new AuxPairOfFiles(Long.parseLong(coluns[0]), coluns[1], coluns[2], coluns[3], coluns[4]);
            //System.out.println(acc.getArq1()+";"+acc.getArq2()+";"+acc.getshaCommitFile1()+";"+acc.getshaCommitFile2());
            AuxTime auxTime = new AuxTime(acc, dao);
            System.out.println(auxTime);
            auxTimeList.add(auxTime);

        }
    }

    private List<AuxTimeFinal> setCountList() {

        List<AuxTimeFinal> countList = new ArrayList<>();
        for (AuxPairOfFiles auxCoChanged : coChangeCountMap.keySet()) {
//            System.out.println("File 1 " + auxCoChanged.getArq1() + " date: " + AuxRepositoryCommit.getRepositoryCommitBySha(dao, auxCoChanged.getshaCommitFile1())
//                    .getCommit().getCommitter().getDateCommitUser()
//                    + " sha1 " + auxCoChanged.getshaCommitFile1());
//            System.out.println("File 1 URL: " + AuxRepositoryCommit.getRepositoryCommitBySha(dao, auxCoChanged.getshaCommitFile1())
//                    .getUrl());
//            System.out.println("File 2 " + auxCoChanged.getArq1() + "  date: " + AuxRepositoryCommit.getRepositoryCommitBySha(dao, auxCoChanged.getshaCommitFile2())
//                    .getCommit().getCommitter().getDateCommitUser()
//                    + " sha1 " + auxCoChanged.getshaCommitFile2());
//            System.out.println("File 2 URL: " + AuxRepositoryCommit.getRepositoryCommitBySha(dao, auxCoChanged.getshaCommitFile2())
//                    .getUrl());
            countList.add(new AuxTimeFinal(auxCoChanged, coChangeCountMap.get(auxCoChanged)));
        }
        return countList;
    }

    private void countCoChanges() {

        for (AuxTime auxcochange : auxTimeList) {
            int count = 0;
            PeriodOfDayCount periodOfDayCount = new PeriodOfDayCount();
            DayOfWeekCount dayOfWeekCount = new DayOfWeekCount();
            DayOfMonthCount dayOfMonthCount = new DayOfMonthCount();
            MonthCount monthCount = new MonthCount();

            if (!coChangeCountMap.containsKey(auxcochange.getAuxPairOfFiles())) {

//                System.out.println("COUNT: " + count);
//                System.out.println("File 1 atual: " + auxcochange.getAuxPairOfFiles().getArq1()
//                        + " - " + auxcochange.getAuxPairOfFiles().getshaCommitFile1());
//                System.out.println("File 2 atual: " + auxcochange.getAuxPairOfFiles().getArq2()
//                        + " - " + auxcochange.getAuxPairOfFiles().getshaCommitFile2());
                for (int j = 0; j < auxTimeList.size(); j++) {
                    if (auxTimeList.get(j).getAuxPairOfFiles().equals(auxcochange.getAuxPairOfFiles())) {
                        count++;

//                        System.out.println("COUNT: " + count);
//                        System.out.println("File 1: " + auxTimeList.get(j).getAuxPairOfFiles().getArq1()
//                                + " - " + auxcochange.getAuxPairOfFiles().getshaCommitFile1());
//                        System.out.println("File 2: " + auxTimeList.get(j).getAuxPairOfFiles().getArq2()
//                                + " - " + auxcochange.getAuxPairOfFiles().getshaCommitFile2());

                        periodOfDayCount.countPeriodOfDay(auxTimeList.get(j).getPedriodOfDayFile1());
                        // periodOfDayCount.countPeriodOfDay(auxcochange.getPedriodOfDayFile2());

                        dayOfWeekCount.countDayOfWeek(auxTimeList.get(j).getDayOfWeekFile1());
                        //dayOfWeekCount.countDayOfWeek(auxcochange.getDayOfWeekFile1());

                        dayOfMonthCount.countDayOfMonth(auxTimeList.get(j).getDayOfMonthFile1());
                        //dayOfMonthCount.countDayOfMonth(auxcochange.getDayOfMonthFile2());

                        monthCount.countMonth(auxTimeList.get(j).getMonthOfModFile1());
                        //monthCount.countMonth(auxcochange.getMonthOfModFile2());
                        
//                        System.out.println(auxTimeList.get(j).getPullRequest().getNumber());

                    }
                }
                coChangeCountMap.put(auxcochange.getAuxPairOfFiles(),
                        new AuxCountCoChange(count, periodOfDayCount, dayOfWeekCount, dayOfMonthCount, monthCount));
            }

        }

    }

    @Override
    public String getHeadCSV() {
        return "issue;file1;file2;sha1;sha2;count;"
                + PeriodOfDayCount.getHeadCSV() + DayOfWeekCount.getHeadCSV() + DayOfMonthCount.getHeadCSV()
                + MonthCount.getHeadCSV();
    }

    @Override
    public List<String> getAvailableMatricesPermitted() {
        return Arrays.asList(CoChangedFileServices.class.getName());
    }

}
