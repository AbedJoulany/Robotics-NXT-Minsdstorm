import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import minimize
from scipy.spatial import distance
import math, json, struct
from springs_net_simulation import  SpringsNetSimulation
from sklearn.linear_model import RANSACRegressor
from area_map import AreaMap
#procrustes scipy

file_path = 'lf22393.csv'
#file_path = 'lf1219428.csv'


data = pd.read_csv(file_path, names=["TimePoint", "State", "x", "y", "course", "heading", "lightValue", "rangeValue"])



# Function to calculate obstacle position
def calculate_obstacle_position(x, y, heading, range_value):
    heading_rad = math.radians(heading + 90)
    obstacle_x = x + range_value * math.cos(heading_rad)
    obstacle_y = y + range_value * math.sin(heading_rad)
    return obstacle_x, obstacle_y


def group_positions():
    global data
    robot_x, robot_y, obstacle_x, obstacle_y, courses = {}, {}, {}, {}, {}

    for index, row in data.iterrows():
        if  row['State'] == "PENDING":
            continue
        if  row['State'].startswith("THIRD_LINE") or row['State'].startswith("THIRD_LINE"):
            row['State'] = f"ON_SIDE"

        group_key = f"{row['State']}_{round(row['course'])}"
        if group_key not in robot_x:
            robot_x[group_key], robot_y[group_key], obstacle_x[group_key], obstacle_y[group_key] = [], [], [], []

        x = row['x']
        y = row['y']
        course = row['course']
        heading = row['heading']
        range_value = row['rangeValue']+10

        robot_x[group_key].append(x)
        robot_y[group_key].append(y)
        courses[group_key] = course

        ox, oy = calculate_obstacle_position(x, y, heading, range_value)
        obstacle_x[group_key].append(ox)
        obstacle_y[group_key].append(oy)
    return robot_x, robot_y, obstacle_x, obstacle_y, courses


def get_least_squares_line(X, Y):
    A = np.vstack([X, np.ones(len(X))]).T
    alpha = np.linalg.lstsq(A, Y, rcond=None)[0]
    return alpha

def get_ransac_line(x,y):
    ransac = RANSACRegressor()
    X = np.array(x)
    X = X.reshape(-1, 1)
    Y = np.array(y)
    ransac.fit(X, Y)

    line_X = np.arange(X.min(), X.max())[:, np.newaxis]
    line_Y = ransac.predict(line_X)

    return (line_X, line_Y,[
                ransac.estimator_.coef_[0],   # slope (m)
                ransac.estimator_.intercept_  # intercept (b)
            ])


def extract_corners(robot_x, robot_y):
    corner_pos_x = []
    corner_pos_y = []
    for key in robot_positions_x:
        if key.startswith('BUMPED'):
            corner_pos_x.append(robot_x[key][0])
            corner_pos_y.append(robot_y[key][0])
    corner_pos_x.append(corner_pos_x[0])
    corner_pos_y.append(corner_pos_y[0])
    return corner_pos_x, corner_pos_y


def extract_courses(robot_x, robot_y, courses):
    corner_pos = {}
    for key in robot_positions_x:
        if key.startswith('ON_SIDE'):
            length = math.sqrt(
                math.pow((robot_x[key][0] - robot_x[key][-1]), 2) + math.pow((robot_y[key][0] - robot_y[key][-1]), 2))
            corner_pos[key] = (robot_x[key][0], robot_y[key][0], courses[key], length)
    return corner_pos


def plot_course_lines(corners):
    for key, corner in corners.items():
        course_rad = math.radians(corner[2])
        end_x = corner[0] + corner[3] * math.cos(course_rad)
        end_y = corner[1] + corner[3] * math.sin(course_rad)

        # Plot the line
        plt.plot([corner[0], end_x], [corner[1], end_y], linestyle='dashed', color='green')


def intersection_point(line1, line2):
    x = (line2[1] - line1[1]) / (line1[0] - line2[0])
    y = line1[0] * x + line1[1]
    return x, y


def show_arc_length(points):
    points = np.vstack([points, points[0]])
    differences = np.diff(points, axis=0)
    distances = np.linalg.norm(differences, axis=1)
    print("acc length:", distances)

def create_map_file(points, start, end, center):
    with open("map.bin", 'wb') as file:
        file.write(struct.pack('>i', len(points)))
        for p in points.tolist():
            print(p)
            file.write(struct.pack('>f', round(p[0], 1)))
            file.write(struct.pack('>f', round(p[1], 1)))


        file.write(struct.pack('>f', round(start[1][0], 1)))
        file.write(struct.pack('>f', round(start[1][1], 1)))
        file.write(struct.pack('>f', round(end[1][0], 1)))
        file.write(struct.pack('>f', round(end[1][1], 1)))
        file.write(struct.pack('>f', round(center[0], 1)))
        file.write(struct.pack('>f', round(center[1], 1)))

        print(f"center {center[0]}, {center[1]}")

        map_file = open("map.json", "w")
        json.dump({
            'points': np.round(points, 1).tolist(),
            'start': np.round(start[1], 1).tolist(),
            'end': np.round(end[1], 1).tolist(),
            'center': np.round(center, 1).tolist()
        }, map_file, indent=6)



def read_points_from_file(filename):
    points = []
    with open(filename, 'rb') as file:
        num_points = struct.unpack('>i', file.read(4))[0]
        print(f'Number of Points: {num_points}')

        for _ in range(num_points):
            x = struct.unpack('>f', file.read(4))[0]
            y = struct.unpack('>f', file.read(4))[0]
            points.append((x, y))
            print(f'Point: ({x}, {y})')

        startx = struct.unpack('>f', file.read(4))[0]
        starty = struct.unpack('>f', file.read(4))[0]
        endx = struct.unpack('>f', file.read(4))[0]
        endy = struct.unpack('>f', file.read(4))[0]
        centerx = struct.unpack('>f', file.read(4))[0]
        centery = struct.unpack('>f', file.read(4))[0]

        print(f'Start Point: ({startx}, {starty})')
        print(f'End Point: ({endx}, {endy})')
        print(f'Center Point: ({centerx}, {centery})')


robot_positions_x, robot_positions_y, obstacle_positions_x, obstacle_positions_y, courses = group_positions()
courses = extract_courses(robot_positions_x, robot_positions_y, courses)
corner_pos_x, corner_pos_y = extract_corners(robot_positions_x, robot_positions_y)

# Create the plot
plt.figure(figsize=(10, 10))
ls_lines = []
starting_point, ending_point =None, None
for group_key in robot_positions_x:
    if group_key.startswith('PENDING') or  group_key.startswith('STARTED'):
        continue

    plt.scatter(robot_positions_x[group_key], robot_positions_y[group_key], c='grey', s=1)
    if group_key.startswith('ON_CORNER'):
        color = 'yellow'
    elif group_key.startswith("ON_CORNER_BACKWARD") :
        color = 'orange'
    else:
        color = 'red'
    plt.scatter(obstacle_positions_x[group_key], obstacle_positions_y[group_key], c=color, s=1)
    if group_key.startswith('FIRST_LINE'):
        line_1 = np.array([robot_positions_x[group_key][0], robot_positions_y[group_key][0]])

    if group_key.startswith('SECOND_LINE'):
        starting_point = np.array([robot_positions_x[group_key][0], robot_positions_y[group_key][0]])
        starting_point_obstacle = [obstacle_positions_x[group_key][0], obstacle_positions_y[group_key][0]]
        line_2 = np.array([robot_positions_x[group_key][0], robot_positions_y[group_key][0]])
        plt.scatter(robot_positions_x[group_key][0], robot_positions_y[group_key][0], c='green', s=20)

    if group_key.startswith('FOURTH_LINE') or group_key.startswith('FINISHED_MAPPING'):
        ending_point = np.array([robot_positions_x[group_key][0], robot_positions_y[group_key][0]])
        ending_point_obstacle = [obstacle_positions_x[group_key][0], obstacle_positions_y[group_key][0]]

        plt.scatter(robot_positions_x[group_key][0], robot_positions_y[group_key][0], c='purple', s=20)

    if group_key.startswith('ON_SIDE'):
        #print(group_key)
        x,y, alpha =  get_ransac_line(obstacle_positions_x[group_key], obstacle_positions_y[group_key])
        ls_lines.append(alpha)
        plt.plot(x, y, color='blue', linestyle='dashed')

    #plot_course_lines(courses)
    plt.scatter(corner_pos_x, corner_pos_y, c='black', s=10)
    #plt.plot(corner_pos_x, corner_pos_y, color='black')

intersection_points_x, intersection_points_y = [], []
#ls_lines = ls_lines[:-1] # remove last line since it is broken -last -> finish finsh-start
#print(ls_lines)
for i in range(len(ls_lines)-1):
    x, y = intersection_point(ls_lines[i], ls_lines[(i + 1) % len(ls_lines)])
    intersection_points_x.append(x)
    intersection_points_y.append(y)

intersection_points_x.insert(0,starting_point_obstacle[0])
intersection_points_y.insert(0,starting_point_obstacle[1])
intersection_points_x.append(ending_point_obstacle[0])
intersection_points_y.append(ending_point_obstacle[1])

plt.plot(intersection_points_x, intersection_points_y, color='blue', linestyle='dashed')
plt.scatter(intersection_points_x, intersection_points_y, c='red', s=50)

points = np.empty((len(intersection_points_x), 2))
connections = []
for  k, p in enumerate(intersection_points_x):
    points[k] = [intersection_points_x[k], intersection_points_y[k] ]

num_points = len(points)
for k in range(num_points-1):
    connections.append((k, k+1 ))

spn = SpringsNetSimulation(points, connections)
error_vector = (ending_point - starting_point)
#print(error_vector)

#spn.move_node(num_points-1, 2*error_vector)


plt.scatter(points[:,0], points[:,1], c='blue', s=30)


spn.simulate(dt=0.5, epsilon=0.2, max_iterations=300)

points = spn.get_final_positions()
#print(points)
plt.scatter(points[:,0], points[:,1], c='green', s=30)


print('normalize map')
am = AreaMap(points, line_1, line_2)
data = am.generate_map()

points = data['points']
start = data['start']
end = data['end']
center = data['center']

#============ for debug =============
"""
target_points = np.array([
    [0, 0],
    [-113.0, 0],
    [-113.0, 40.0],
    [-78, 77],
    [0, 77]
])
target_center = np.mean(points, axis=0)
"""
#============ for debug =============

#============ for debug =============
target_points = np.array([
    [0, 0],
    [-101.0, 0],
    [-190.0, 89.0],
    [-190, 237],
    [0, 237]
])
target_center =np.array([-95, 118.5])
#============ for debug =============


create_map_file(points=points, start=start, end=end, center=center)
show_arc_length(points)

points = np.vstack([points, points[0]])
plt.plot(points[:,0], points[:,1], color='black', linestyle='dashed')
plt.plot(start[:,0], start[:,1] , color='black', linestyle='dashed')
plt.plot(end[:,0], end[:,1] , color='black')
plt.scatter(center[0], center[1], c='grey', s=30)


target_points = np.vstack([target_points, target_points[0]])
plt.plot(target_points[:,0], target_points[:,1], color='pink', linestyle='dashed')
plt.scatter(target_center[0], target_center[1], c='pink', s=30)



# Labels and legend
plt.xlabel('X Position')
plt.ylabel('Y Position')
plt.title('Robot and Obstacle Positions')
#plt.legend()
plt.grid(True)

# Show the plot
plt.savefig(file_path + ".png")
plt.close()



#read_points_from_file('map.bin')