#-----------------------------------------------------------------------
#			CS 496 - Mobile and Cloud Software Development
#				Oregon State University - Summer 2016
#                     How-To-Guide & Final Project
#
# Author: James Pool
# ONID: 932664412
# OSU Email: poolj@oregonstate.edu
# Date: 11 August 2016
#
# Application Name: poolj-cs496-colors
# Filename: login.py
# Description: Route file for Login class
#
# Referance:
# https://developers.google.com/identity/sign-in/android/backend-auth
#-----------------------------------------------------------------------

import webapp2
from google.appengine.ext import ndb
import db_defs
import json
from google.appengine.ext import vendor
vendor.add('lib')
from oauth2client import client, crypt

CLIENT_ID = json.loads(
    open('client_secret.json','r').read())['web']['client_id']

class Login(webapp2.RequestHandler):
    def post(self, *args):
        # -----------------------------------------------------------------------------
        # Verifies a token ID from Google using Google API client library
        #   POST Body Variables:
        #       name - Full Name (required) [string]
        # -----------------------------------------------------------------------------
        # Handle accept types
        if 'application/json' not in self.request.accept:
            self.response.status = 406
            self.response.body = "Not Acceptable: API only supports application/json"
            return
        
        # Get token from POST body
        tokenID = self.request.get('tokenID', default_value=None)
        
        # Check POST body variables
        if not tokenID:
            self.response.status = 400
            self.response.body = "Bad Request: Token ID is required"
            return

        # Verify Token
        try:
            idInfo = client.verify_id_token(tokenID, CLIENT_ID)
            if idInfo['aud'] != CLIENT_ID:  # Web is only authorizing client
                raise crypt.AppIdentityError("Unrecognized client")
            if idInfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
                raise crypt.AppIdentityError("Wrong issuer")                  
        except crypt.AppIdentityError as e:
            self.response.status = 401
            self.response.write(str(e))  # Write Error
            return
            
        # Send Google sub ID back
        userID = {}
        userID['userID'] = idInfo['sub']
        self.response.status = 200
        self.response.write(json.dumps(userID))
            
        return