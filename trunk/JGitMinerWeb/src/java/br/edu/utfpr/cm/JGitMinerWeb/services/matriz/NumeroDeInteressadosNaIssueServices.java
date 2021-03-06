/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.edu.utfpr.cm.JGitMinerWeb.services.matriz;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.model.EntityNode;
import br.edu.utfpr.cm.JGitMinerWeb.model.miner.EntityRepository;
import br.edu.utfpr.cm.JGitMinerWeb.services.matriz.auxiliary.AuxNumberInteressados;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author geazzy
 */
public class NumeroDeInteressadosNaIssueServices extends AbstractMatrizServices{

      private List<AuxNumberInteressados> issueList;

    public NumeroDeInteressadosNaIssueServices(GenericDao dao, OutLog out) {
        super(dao, out);
        issueList = new ArrayList<>();
    }

    public NumeroDeInteressadosNaIssueServices(GenericDao dao, EntityRepository repository, Map params, OutLog out) {
        super(dao, repository, params, out);
        issueList = new ArrayList<>();
    }

    

    @Override
    public void run() {
        System.out.println(params);

        if (getRepository() == null) {
            throw new IllegalArgumentException("Parâmetro Repository não pode ser nulo.");
        }
        
        if (getMilestoneNumber() > 0) {
            getIssuesByMilestone();
        } else {
            getIssuesByDate();
        }

        obterQuantidadeDeInteressados();

        
        addToEntityMatrizNodeList(issueList);

    }

    @Override
    public String getHeadCSV() {
        return "IssueNumber;NumInteressados;url";
    }

    private void getIssuesByMilestone() {
          int mileNumber = new Integer(getMilestoneNumber());

        if (mileNumber <= 0) {
            throw new IllegalArgumentException("Numero do Milestone inválido.");
        }

        String jpql = "SELECT NEW " + AuxNumberInteressados.class.getName() + "(p.issue.id, p.number, p.issue.url) "
                + "FROM "
                + "EntityPullRequest p "
                + "WHERE "
                + "p.repository = :repository  AND "
                + "p.issue.commentsCount > 0  AND "
                + (mileNumber > 0 ? "p.issue.milestone.number = :milestoneNumber " : "")
                + " GROUP BY p.number ";

        System.out.println(jpql);

        List<AuxNumberInteressados> query = dao.selectWithParams(jpql,
                new String[]{
                    "repository",
                    mileNumber > 0 ? "milestoneNumber" : "#none#",},
                new Object[]{
                    getRepository(),
                    mileNumber,});

        System.out.println("query: " + query.size());

        this.issueList = query;

    }

    private void getIssuesByDate() {
      
        String jpql = "SELECT NEW " + AuxNumberInteressados.class.getName() + "(p.issue.id, p.number, p.issue.url) "
                + "FROM "
                + "EntityPullRequest p "
                + "WHERE "
                + "p.repository = :repository  AND "
                + "p.issue.commentsCount > 0  AND "
                + "p.issue.createdAt >= :dataInicial AND "
                + "p.issue.createdAt <= :dataFinal "
                + " GROUP BY p.number ";

        System.out.println(jpql);

        List<AuxNumberInteressados> query = dao.selectWithParams(jpql,
                new String[]{
                    "repository",
                    "dataInicial",
                    "dataFinal"
                }, new Object[]{
                    getRepository(),
                    getBeginDate(),
                    getEndDate()
                });

        System.out.println("query: " + query.size());

        this.issueList = query;
    }

    private void obterQuantidadeDeInteressados() {
      
        for (AuxNumberInteressados issue : issueList) {
            String jpql3 = "SELECT count(ie) "
                        + "FROM "
                        + "EntityIssueEvent ie "
                        + "WHERE "
                        + "ie.issue.id = :id "
                    + " and ie.event = 'subscribed'";
            
            System.out.println("jpql3: " + jpql3);

                issue.setQuantidadeDeInteressados(((Long)(dao.selectWithParams(jpql3,
                        new String[]{
                            "id",}, new Object[]{
                            issue.getId(),}) ).get(0)).intValue());
                
                System.out.println("number: "+issue.getIssueNumber()+" ID: "+issue.getId());
        }
    }
    
}
