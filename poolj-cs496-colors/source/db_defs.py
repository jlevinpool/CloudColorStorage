#-----------------------------------------------------------------------
#			CS 496 - Mobile and Cloud Software Development
#				Oregon State University - Summer 2016
#                     How-To-Guide & Final Project
#
# Author: James Pool
# ONID: 932664412
# OSU Email: poolj@oregonstate.edu
# Date: 7 August 2016
#
# Application Name: poolj-cs496-colors
# Filename: db_def.py
# Description: Source for database definitions
#-----------------------------------------------------------------------

from google.appengine.ext import ndb

class Model(ndb.Model):
    def to_dict(self):
        d = super(Model, self).to_dict()
        d['key'] = self.key.id()
        return d

class Color(Model):
    title = ndb.StringProperty(required=True)
    value = ndb.IntegerProperty(required=True)
    userKey = ndb.IntegerProperty(required=True)

class User(Model):
    sub = ndb.StringProperty(required=True)
    colors = ndb.KeyProperty(kind=Color, repeated=True)
    
    def to_dict(self):
        d = super(User, self).to_dict()
        d['colors'] = [c.id() for c in d['colors']]
        return d