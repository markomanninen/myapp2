(defproject myapp2 "1.0.0-SNAPSHOT"
  ; deploy /Users/marko/Downloads/appengine-java-sdk-1.4.2/bin/appcfg.sh -e mmstud@gmail.com update war/
  ; java virtual machine update 4 breaks dev_appengine run!
  :author "Marko Manninen"
  :url "http://github.com/r0man/appengine-clj"
  :autodoc {:name "Clojure for Google App Engine"
            :web-src-dir "http://github.com/r0man/appengine-clj/blob/"
            :web-home "http://r0man.github.com/appengine-clj/"
            :copyright "Copyright (c) 2011 Marko Manninen"}
  :description "A clojure web application Google App Engine"
  :source-path "src"
  :compile-path "classes"
  :library-path "war/WEB-INF/lib/"
  :appengine-sdk "/Users/marko/Downloads/appengine-java-sdk-1.4.3/"
  :dependencies [
	 			 [org.clojure/clojure "1.2.1"] ; doesnt count if appengine magic is loaded here
				 [compojure "0.6.2"]
				 [hiccup "0.3.4"]
				 [ring/ring-servlet "0.3.7"] ; for appengine
				 ;;[clj-json "0.3.2"]
				 ;[org.clojars.kriyative/clojurejs "1.2.6"]
				 [org.clojars.scottjad/scriptjure "0.1.20"]
				 ;[cljs "0.2"]
				 ;[swank-clojure "1.3.0-SNAPSHOT"]
				]
  :dev-dependencies [
					 ;[swank-clojure "1.3.0-SNAPSHOT"] ; doesnt work without SNAPSHOT!
					 ;[appengine-magic "0.4.0-SNAPSHOT"]
					 [ring/ring-core "0.3.7"] ; for repl
					 [ring/ring-devel "0.3.7"] ; for debug
					 [ring/ring-jetty-adapter "0.3.7"] ; for repl
					 [ring-serve "0.1.0"] ; for repl
					 ;[appengine "0.4.3-SNAPSHOT"]
					 ;[inflections "0.4.2-SNAPSHOT"] ; dont work alone, but inside appengine it works
					]
  ;:repositories {"maven-gae-plugin" "http://maven-gae-plugin.googlecode.com/svn/repository"}
  :aot [cljgae.core]
  ;:aot [cljgae.servlet]
  ;:aot [cljgae.app_servlet]
)