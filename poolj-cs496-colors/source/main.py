#-----------------------------------------------------------------------
#			CS 496 - Mobile and Cloud Software Development
#				Oregon State University - Summer 2016
#	           				Assignment 3: Web API
#
# Author: James Pool
# ONID: 932664412
# OSU Email: poolj@oregonstate.edu
# Date: 17 July 2016
#
# Application Name: poolj-cs496-a3
# Filename: main.py
# Description: Main source file
#-----------------------------------------------------------------------

import webapp2
from google.appengine.api import oauth

class defaultLanding(webapp2.RequestHandler):
    def get(self):
        # This is a landing page to verify that GAE is functioning
        self.response.write("<html><body>")
        self.response.write("<strong>CS 496 - Mobile and Cloud Software Development</strong><br>")
        self.response.write("<strong>Oregon State University - Summer 2016</strong>")
        self.response.write("<p>How-To-Guide & Final Project</p>")
        self.response.write("<p>James Pool</p>")
        self.response.write("<em>Google App Engine is Working!</em>")
        self.response.write("</body></html>")

application = webapp2.WSGIApplication([
    ('/', 'main.defaultLanding'),
], debug=True)
application.router.add(webapp2.Route(r'/user<:/?>', 'user.User'))
application.router.add(webapp2.Route(r'/user/<id:[0-9]+><:/?>', 'user.User'))
application.router.add(webapp2.Route(r'/user/search<:/?>','user.UserSearch'))
application.router.add(webapp2.Route(r'/color<:/?>', 'color.Color'))
application.router.add(webapp2.Route(r'/color/<id:[0-9]+><:/?>','color.Color'))
application.router.add(webapp2.Route(r'/login<:/?>', 'login.Login'))