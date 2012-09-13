/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.dao;

import br.edu.utfpr.cm.JGitMinerWeb.pojo.EntityUser;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Douglas
 */
@Stateless
public class UserDao extends AbstractDao<EntityUser> {

    @PersistenceContext(unitName = "pu")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserDao() {
        super(EntityUser.class);
    }
}
