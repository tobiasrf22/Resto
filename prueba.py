from  pymongo import MongoClient
import urllib
import pprint

client = MongoClient('localhost', 27017)
db = client['Barrios']
posts = db.Barrios
l = posts.find({},{'location.coordinates':1})

for v in l:
    print(v)