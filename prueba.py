from  pymongo import MongoClient
import urllib
import pprint

client = MongoClient('localhost', 27017)
db = client['Barrios']
posts = db.Barrios
query = {'name':'Avellaneda'}
l = posts.find()
j = {}
for v in l:
    j.update(v)
print(j['location']['coordinates'])