#Using Git
##Intro
Ths will be a short step by step guide for the commandline interface of git, as we are all using mac OS X you should be able to use it. Any GUI for git will support the same actions, just find the right menu =p. 
##Gettting ready to work
Scenario: You have a fork on github and nothing else
To get an intial working copy of the repository you type

	$ git clone git@github.com:[YOUR_USERNAME]]/hats.git

or

	$ git clone https://github.com/[YOUR_USERNAME]]/hats

The first command will retrieve the data via ssl and the second via https. If the first doesnt work use the second.

Now you have a working copy on your computer in the directory where you called the shell command. Your fork on github will automatically be added to the remotes list, i.e. the list of all repositories you can get/push changes to. You can take alook at all remotes of the project like this:

	$ git remote -v
	origin	git@github.com:NemoOudeis/hats.git (fetch)
	origin	git@github.com:NemoOudeis/hats.git (push)
	upstream	git@github.com:doque/hats.git (fetch)
	upstream	git@github.com:doque/hats.git (push)
 
 As you can see my project has my fork (NemoOudeis) and the upstream repo (Doque) as fetch and push remotes. If you don't have the upstream remote in your remotes list do this

 	$ git remote add upstream git@github.com:doque/hats.git

 (if ssl doesn't work for you you can use the https address here as well).

 Your (local) repository should have at least 2 branches, `master`, and `develop`, you can chek this by 

 	$ git branch
 	  develop
	* documentation#38/localGitBranching
 	  feature#9/JsonEndpointCards

 as you can see I have 3 branches (I have no local `master` branch but don't worry about that). The star indicates the checked out (i.e. selected) branch. If you have no `devlop` branch do

 	$ git branch develop

To switch between branches use the `checkout` command

	$ git checkout develop
	Switched to branch 'develop'
	$ git branch
	* develop
  	  documentation#38/localGitBranching
      feature#9/JsonEndpointCards

Next we get all the recent changes from upstream (the central repo). First check out your local `develop` (if you are on a different branch) and then pull the changes from `upstrem/develop`

	$ git checkout develop
	Switched to branch 'develop'
	$ git pull upstream develop
	From github.com:doque/hats
 	* branch            develop    -> FETCH_HEAD
	Already up-to-date.

I am up to date so there is no merging done, but if there were any differneces git would try to merge them and only if there are conflicts you'd need to merge them manually. If this happens use the `mergetool` 
	
	$ git mergetool

which will give a comparison GUI like you are used to from IDEs.

##Starting a feature branch
Now that you are up to date you can start on your on changes. Each feature should have it's own feature branch. From `develop` create a new feature branch by

	$ git branch feature#1234/someHumanReadbleDescription
	$ git branch
	* develop
	  documentation#38/localGitBranching
   	  feature#1234/someHumanReadbleDescription
  	  feature#9/JsonEndpointCards

you can delete a local branch by using `git branch -d [branch name]` if you made some mistake or don't need it anymore. You can switch between branches by `checkout` like shown above.

After you have made some changes to the project (like adding you new feature) you should run `status` to see what files are changed/deleted/new

	$ git status
	# On branch documentation#38/localGitBranching
	# Untracked files:
	#   (use "git add <file>..." to include in what will be committed)
	#
	#	GitTutorial.html
	#	GitTutorial.md

As you can see my workspace has new untracked files that need to be added before I can creade a commit. To add a file use `add [filename]` or just add all changes
	
	$ git add .
	$ git status
	# On branch documentation#38/localGitBranching
	# Changes to be committed:
	#   (use "git reset HEAD <file>..." to unstage)
	#
	#	new file:   GitTutorial.html
	#	new file:   GitTutorial.md

Now the changes are staged and ready to be commited. If you want to unstage something use the `reset` command like the git status output suggests. Commit the changes by `commit`

	$ git commit  	# this will open your default editor for a commit message, which has to be non empty

After commiting `status` will show no changes and you can push the branch to your fork repo `origin`. 	
	$ git push origin [branch name]

will push the status of the currently selected branch to the remotes branch origin/[branch name]. You can check github and see that the branch is available on your fork now. 

If your feature is completed create a pull request on the github web interface. As soon as your feature is merged into a upstream branch you can safeley delete the branch on your fork and locally. 
	
	$ git branch -D [branch name] 		# deletes a local branch that is tracked remotely
	$ git push origin :[branch name] 	# deletes the branch on the repository. Don't Forget the :

If there's something missing tell me! Cheers - Nemo




