EDITOR=vi

default: com/iglooware/PortalAuth.class com/iglooware/Toaster.class com/iglooware/LinkLabel.class

gitconfig:
	git config --global push.default simple
	git config --global credential.helper 'cache --timeout=3600'

checkout:
	#git fetch origin
	#git merge origin/master
	git pull

checkin: # e.g. downwa
	sh checkin.sh
	git commit -v
	git push

run: default
	java -cp . com.iglooware.PortalAuth

com/iglooware/PortalAuth.class: com/iglooware/PortalAuth.java
	javac com/iglooware/PortalAuth.java

com/iglooware/Toaster.class: com/iglooware/Toaster.java
	javac com/iglooware/Toaster.java

com/iglooware/LinkLabel.class: com/iglooware/LinkLabel.java
	javac com/iglooware/LinkLabel.java
