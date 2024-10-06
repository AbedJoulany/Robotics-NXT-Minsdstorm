import numpy as np
import matplotlib.pyplot as plt
import json, random
from area_map import AreaMap
from springs_net_simulation import  SpringsNetSimulation



def rnd(num):
    return num + random.random()*25 - 25
points = np.array(
[
    [rnd(70.0), rnd(0.0)],
    [rnd(140.0), rnd(-120.0)],
    [rnd(140.0), rnd(-210.0)],
    [rnd(-70.0), rnd(-210.0)],
    [rnd(-70), rnd(5)]
  ]
)
print(points)
line_1 = np.array([15,-5])
line_2 = np.array([30,-7])

ending_point = np.array([30,-5])
starting_point = np.array([45,-7])

plt.figure(figsize=(10, 10))
plt.plot(points[:,0], points[:,1], color='blue', linestyle='dashed')



num_points = len(points)
connections = []
for k in range(num_points):
    connections.append((k, (k+1) % num_points ))

spn = SpringsNetSimulation(points, connections)
error_vector = (ending_point - starting_point)
spn.move_node(num_points-1, 2*error_vector)
spn.simulate(dt=0.5, epsilon=0.2)
points = spn.get_final_positions()
plt.scatter(points[:,0], points[:,1], c='green', s=30)


am = AreaMap(points, line_1, line_2)
data = am.generate_map()

points = data['points']
start = data['start']
end = data['end']
center = data['center']

print(json.dumps({
            'points': points.tolist(),
            'start': start.tolist(),
            'end': end.tolist(),
            'center': center.tolist()
        }))

plt.plot(points[:,0], points[:,1], color='black', linestyle='dashed')
plt.plot(start[:,0], start[:,1] , color='black')
plt.plot(end[:,0], end[:,1] , color='black')
plt.scatter(center[0], center[1], c='grey', s=30)



# Labels and legend
plt.xlabel('X Position')
plt.ylabel('Y Position')
plt.title('Robot Map')
#plt.legend()
plt.grid(True)

# Show the plot
plt.savefig("map.png")
plt.close()