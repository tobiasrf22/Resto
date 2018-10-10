from  pymongo import MongoClient
import urllib
import pprint

client = MongoClient('localhost', 27017)
db = client['Barrios']
posts = db.Barrios
l = posts.find()
j = []
for v in l:
    j.append(v)

print(j)