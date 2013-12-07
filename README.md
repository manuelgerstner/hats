Six Thinking Hats
====

Built with Play 2.2.1

###Installation:
* Grab Play 2.2.1 from [here](http://www.playframework.com/download) and move to any &lt;directory&gt; with rw access
* Add &lt;directory/**bin**&gt; to PATH variable
* Clone this Repository and type `play run` to run app
* For use with Eclipse, type `play eclipse` to create files to *Import as Existing Project*
* Guide to configure your IDE is [here](http://scala-ide.org/docs/tutorials/play/), this part is important:  

![image](http://scala-ide.org/docs/_images/refresh-on-access.png)

###Running/Debugging the Application:
* Navigate to application directory
* Run `play debug run` to start the application
* In Eclipse, add a new Debug Configuration with the parameters:
	* Remote Java Application
	* Connection Type: Standard (Socket Attach)
	* Host: `localhost`
	* Port: `9999`

##Git & Collab

###Branches & Forks
For a clean and proper collaboration the following workflow is used:

* Set your git to `no-ff` merges by `git config --global --add merge.ff false`
* The original (doque's) repo will be the central repository called `upstream`
* Everybody forks the repo and clone his/her own fork to local
* Branches: `master` is a stable (i.e. release) branch, `develop` is something between alpha and RC and there will be several `featureXYZ` branches.
* For every feature (or feature group) you create a branch based on the current `develop` (i.e. your `develop` branch based on the most recent `upstream/development` branch). If necessary `pull`, `fetch` & `merge` or `rebase` your `develop` on `upstream/develop` before you create the new feature branch
* When a branch is ready to be merged you `fetch upstream/develop` and `rebase` your branch on it and then create a pull request. If there are conflicts **you** have to solve them before creating the pull request to `upstream/develop`

If you care why we decided to do it like this refer to [here for branching patterns](https://blogs.atlassian.com/2013/05/git-branching-and-forking-in-the-enterprise-why-fork/) and [here for no-ff](http://www.relativesanity.com/articles/ffwd)

###Commits

For commit messages a simple pattern is used:

* First line contains a classification and a short description. The classification may be refactor, style, fix, feature, documentation (mey be extended later on). Limit the first line to 50 characters, so it will be displayed nicely on github
* After that there will be a blank line followed a more detailed description in the 3rd line (and any further lines).

For example:

```
docu: adds info about git
    
Gives info about branching pattern, fork behaviour
and git settings and commit messages
```

Using this pattern will make the overall log is very readable and it is easy to automate the change log creation.