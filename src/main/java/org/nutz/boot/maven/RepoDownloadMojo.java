package org.nutz.boot.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;

@Mojo(name = "repo-download")
public class RepoDownloadMojo extends AbstractRepoMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (file == null) {
            if (project == null)
                throw new MojoFailureException("require repo.file or pom.xml!!!");
            file = project.getArtifact().getFile();
            if (file == null) {
                file = new File("target/" + repoAppName + '-' + repoAppVersion + ".jar");
            }
        }
        if (!file.exists())
            Files.createDirIfNoExists(file.getParentFile());
        Log log = getLog();
        try (OutputStream out = new FileOutputStream(file)) {
            String url = repoUrl + "/" + repoUser + "/" + repoAppName + "/" + repoAppVersion + "/" + file.getName() + "/";
            log.info("Download URL=" + url);
            log.info("Downloading... ");
            Response resp = Http.get(url);
            if (resp.isOK()) {
                Streams.write(out, resp.getStream());
            } else {
                getLog().info("Download FAIL!!! respCode=" + resp.getStatus() + "\r\n" + resp.getContent());
                throw new MojoFailureException("respCode=" + resp.getStatus());
            }
        }
        catch (IOException e) {
            getLog().info("Download FAIL!!!", e);
            throw new MojoFailureException(e.getMessage(), e);
        }
        catch (Throwable e) {
            getLog().info("Download FAIL!!!", e);
            throw new MojoFailureException(e.getMessage(), e);
        }
    }
}
