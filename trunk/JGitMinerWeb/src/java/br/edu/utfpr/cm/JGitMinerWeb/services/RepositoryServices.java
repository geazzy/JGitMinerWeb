/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.pojo.EntityRepository;
import br.edu.utfpr.cm.JGitMinerWeb.util.out;
import java.util.Date;
import java.util.List;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 *
 * @author Douglas
 */
public class RepositoryServices {

    private static EntityRepository getRepositoryByIdRepository(Long idRepo, GenericDao dao) {
        List<EntityRepository> users = dao.executeNamedQueryComParametros("Repository.findByIdRepository", new String[]{"idRepository"}, new Object[]{idRepo});
        if (!users.isEmpty()) {
            return (EntityRepository) dao.findByID(users.get(0).getId(), EntityRepository.class);
        }
        return null;
    }

    public static EntityRepository createEntity(Repository gitRepository, GenericDao dao, boolean primary) {
        if (gitRepository == null) {
            return null;
        }

        EntityRepository repo = getRepositoryByIdRepository(gitRepository.getId(), dao);

        if (repo == null) {
            repo = new EntityRepository();
        }

        repo.setMineredAt(new Date());
        repo.setPrimaryMiner(primary);
        repo.setFork(gitRepository.isFork());
        repo.setHasDownloads(gitRepository.isHasDownloads());
        repo.setHasIssues(gitRepository.isHasIssues());
        repo.setHasWiki(gitRepository.isHasWiki());
        repo.setIsPrivate(gitRepository.isPrivate());
        repo.setCreatedAt(gitRepository.getCreatedAt());
        repo.setPushedAt(gitRepository.getPushedAt());
        repo.setUpdatedAt(gitRepository.getUpdatedAt());
        repo.setIdRepository(gitRepository.getId());
        repo.setSizeRepository(gitRepository.getSize());
        repo.setCloneUrl(gitRepository.getCloneUrl());
        repo.setDescription(gitRepository.getDescription());
        repo.setHomepage(gitRepository.getHomepage());
        repo.setGitUrl(gitRepository.getGitUrl());
        repo.setHtmlUrl(gitRepository.getHtmlUrl());
        repo.setLanguageRepository(gitRepository.getLanguage());
        repo.setMasterBranch(gitRepository.getMasterBranch());
        repo.setMirrorUrl(gitRepository.getMirrorUrl());
        repo.setName(gitRepository.getName());
        repo.setSshUrl(gitRepository.getSshUrl());
        repo.setSvnUrl(gitRepository.getSvnUrl());
        repo.setUrl(gitRepository.getUrl());
        repo.setOwner(UserServices.createEntity(gitRepository.getOwner(), dao));
        repo.setParent(RepositoryServices.createEntity(gitRepository.getParent(), dao, false));
        repo.setSource(RepositoryServices.createEntity(gitRepository.getSource(), dao, false));

        if (repo.getId() == null || repo.getId().equals(new Long(0))) {
            dao.insert(repo);
        } else {
            dao.edit(repo);
        }

        return repo;
    }

    public static Repository getGitRepository(String ownerLogin, String repoName) throws Exception {
        return new RepositoryService().getRepository(ownerLogin, repoName);
    }

    public static List<Repository> getForksFromRepository(Repository gitRepo) throws Exception {
        out.printLog("Baixando Forks...\n");
        List<Repository> forks = new RepositoryService().getForks(gitRepo);
        out.printLog(forks.size() + " Forks baixados!");
        return forks;
    }
}
