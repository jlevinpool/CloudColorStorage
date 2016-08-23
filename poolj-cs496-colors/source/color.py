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
# Filename: color.py
# Description: Route file for Color class
#-----------------------------------------------------------------------

import webapp2
from google.appengine.ext import ndb
import db_defs
import json

class Color(webapp2.RequestHandler):
    def get(self, *args, **kwargs):
        # -----------------------------------------------------------------------------
        # List Color entities
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        
        # Check if an ID was passed
        if 'id' in kwargs:
            message = ndb.Key(db_defs.Color, int(kwargs['id'])).get().to_dict()
            self.response.write(json.dumps(message))
        else: # Return all the keys
            qry = db_defs.Color.query()
            keys = qry.fetch(keys_only=True)
            message = { 'keys' : [x.id() for x in keys] }
            self.response.write(json.dumps(message))
        
        return
        
    def post(self, *args):
        # -----------------------------------------------------------------------------
        # Creates a new Color entity
        #   POST Body Variables:
        #       title - Color Title (required) [string]
        #       value - Color Value (required) [int]
        #       userKey - Associated User Entity Key (required) [int]
        # -----------------------------------------------------------------------------
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        
        # Get user entity key
        userKey = self.request.get('userKey', default_value=None)
        
        # Check if userID exists
        if not userKey:
            self.response.status = 400
            self.response.body = "Bad Request: User ID is required"
            return
        
        # Verify userID matches an existing User entity
        user = ndb.Key(db_defs.User, int(userKey)).get()
        if not user:
            self.response.status = 404
            self.response.body = "Not Found: No User matches the ID"
            return
        
        # Create new Color entity & read POST body variables
        newColor = db_defs.Color()
        title = self.request.get('title', default_value=None)
        value = self.request.get('value', default_value=None)
        
        if title: # Color Title (required)
            newColor.title = title
        else:
            self.response.status = 400
            self.response.body = "Bad Request: Color title is required"
            return
        if value: # Color Value (required)
            newColor.value = int(value)
        else:
            self.response.status = 400
            self.response.body = "Bad Request: Color value is required"
            return
        
        newColor.userKey = int(userKey)  # User key (required) - Already verified
        
        # Store new Color entity
        key = newColor.put()
        
        # Append to User
        user.colors.append(key)
        user.put()
        message = newColor.to_dict()
        self.response.write(json.dumps(message))
        
        return
        
    def put(self, *args):
        # -----------------------------------------------------------------------------
        # Updates a Color entity at the given key
        #   PUT Body Variables:
        #       key - NDB Key (required) [int]
        #       title - Color Title (optional) [string]
        #       value - Color Value (optional) [int]
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        
        # Read PUT body variables
        key = self.request.get('key', default_value=None)
        title = self.request.get('title', default_value=None)
        value = self.request.get('value', default_value=None)
        
        # Check if ID exists
        if not key: # NDB Key (required)
            self.response.status = 400
            self.response.body = "Bad Request: Color ID is required"
            return
        
        # Check if a User Entity was found
        updateColor = ndb.Key(db_defs.Color, int(key)).get()
        if not updateColor:
            self.response.status = 404
            self.response.body = "Not Found: No Color matches the ID"
            return
        if title: # Color title (optional for update)
            updateColor.title = title
        if value: # Color value (optional for update)
            updateColor.value = int(value)
        
        # Store updated Color entity
        result = updateColor.put()
        message = updateColor.to_dict()
        self.response.write(json.dumps(message))
        
        return
        
    def delete(self, *args, **kwargs):
        # -----------------------------------------------------------------------------
        # Deletes a Color entity at the given key
        #  DELETE Body Variables:
        #       key - NDB Key (required) [int]
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        
        # Check if a key was passed
        if 'id' in kwargs:
            deleteColor = ndb.Key(db_defs.Color, int(kwargs['id'])).get()
            # Check if a Color Entity was found
            if not deleteColor:
                self.response.status = 404
                self.response.body = "Not Found: No Color matches the key"
                return
        else:  # Key not passed
            self.response.status = 400
            self.response.body = "Bad Request: Color key is required"
            return
                
        # Delete Color from Associated User
        colorUser = ndb.Key(db_defs.User, deleteColor.userKey).get()
        for index, c in enumerate(colorUser.colors):
            if (deleteColor.key.id() == c.id()):
                colorUser.colors.pop(index)  # Remove from list
                colorUser.put()  # Update
                  
        # Delete Color Entity
        message = deleteColor.to_dict()
        ndb.Key(db_defs.Color, int(kwargs['id'])).delete() 
        self.response.write(json.dumps(message))
        
        return