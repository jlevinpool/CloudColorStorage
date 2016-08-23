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
# Filename: user.py
# Description: Route file for User class
#
# Referances:
# https://developers.google.com/identity/protocols/OpenIDConnect
#-----------------------------------------------------------------------

import webapp2
from google.appengine.ext import ndb
import db_defs
import json

class User(webapp2.RequestHandler):
    def get(self, *args, **kwargs):
        # -----------------------------------------------------------------------------
        # List User entities
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
       
       # Check if a key was passed
        if 'id' in kwargs:
            message = ndb.Key(db_defs.User, int(kwargs['id'])).get().to_dict()
            self.response.write(json.dumps(message))
        else: # Return all the keys
            qry = db_defs.User.query()
            keys = qry.fetch(keys_only=True)
            message = { 'keys' : [x.id() for x in keys] }
            self.response.write(json.dumps(message))
        
        return
            
    def post(self, *args):
        # -----------------------------------------------------------------------------
        # Creates a new User entity
        #   POST Body Variables:
        #       sub - Google sub Identifier  (required) [String]
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        
        # Create new User entity & read POST body variables
        newUser = db_defs.User()
        sub = self.request.get('sub', default_value=None)
        
        # Check POST body variables
        if sub: # Google sub (required)
            newUser.sub = sub
        else:
            self.response.status = 400
            self.response.body = "Bad Request:Google sub identifier is required"
            return
        
        # Store new User entity
        key = newUser.put()
        message = newUser.to_dict()
        self.response.write(json.dumps(message))
        
        return
        
    def put(self, *args):
        # -----------------------------------------------------------------------------
        # Updates a User entity at the given key
        #   PUT Body Variables:
        #       key - NDB Key (required) [int]
        #       sub - Google sub Identifier (optional) [String]
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        
        # Read PUT body variables
        key = self.request.get('key', default_value=None)
        sub = self.request.get('sub', default_value=None)
        
        # Check if key exists
        if not key: # NDB Key (required)
            self.response.status = 400
            self.response.body = "Bad Request: User key is required"
            return
       
       # Check if a Entity was found
        updateUser = ndb.Key(db_defs.User, int(key)).get()
        if not updateUser:
            self.response.status = 404
            self.response.body = "Not Found: No User matches the key"
            return
        if  sub: # Google sub (optional for update)
            updateUser.sub = sub
        
        # Store updated User entity
        result = updateUser.put()
        message = updateUser.to_dict()
        self.response.write(json.dumps(message))
        
        return
        
    def delete(self, *args, **kwargs):
        # -----------------------------------------------------------------------------
        # Deletes a User entity at the given key
        #  DELETE Body Variables:
        #       key - NDB Key (required) [int]
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        
        # Check if an ID was passed
        if 'id' in kwargs:
            deleteUser = ndb.Key(db_defs.User, int(kwargs['id'])).get()
            # Check if a User Entity was found
            if not deleteUser:
                self.response.status = 404
                self.response.body = "Not Found: No User matches the key"
                return
        else:  # Key not passed
            self.response.status = 400
            self.response.body = "Bad Request: User key is required"
            return
        
        # Delete Associated Color Entites
        for c in deleteUser.colors:
            c.delete()
                
        # Delete User Entity
        message = deleteUser.to_dict()
        ndb.Key(db_defs.User, int(kwargs['id'])).delete()
        self.response.write(json.dumps(message))
        
        return
        
class UserSearch(webapp2.RequestHandler):
    def post(self, *args):
        # -----------------------------------------------------------------------------
        # Search for User entities
        #   POST Body Variables
        #       sub - Google sub identifier (optional) [String]
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        # Read in POST Body Variables and apply as filters to query
        qry = db_defs.User.query()
        if self.request.get('sub', None):
            qry = qry.filter(db_defs.User.sub == self.request.get('sub')) 
        # Fetch results
        keys = qry.fetch(keys_only=True)
        message = { 'keys' : [x.id() for x in keys] }
        self.response.write(json.dumps(message))