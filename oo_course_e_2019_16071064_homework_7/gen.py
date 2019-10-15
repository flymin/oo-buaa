import random
pair = []
l = [-3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
for i in l:
	for j in l:
		if i == j:
			continue
		else:
			pair.append((i,j))
n = 0
time = 0.0
with open("data.txt", 'a') as f:
	for t in random.sample(pair, 20):
	    print("[{:.1f}]{}-FROM-{}-TO-{}".format(time, n, t[0], t[1]))
	    status = f.write("[{:.1f}]{}-FROM-{}-TO-{}\n".format(time, n, t[0], t[1]))
	    n += 1
	    if random.random() > 0.8:
	    	time += random.random()
	f.write("\n")