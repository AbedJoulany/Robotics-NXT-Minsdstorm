import math

import numpy as np

debug_enabled = False



class SpringsNetSimulation:
    def __init__(self, vertices, connections):
        self.vertices = vertices
        self.connections = []
        curve_connections = []
        self.node_mass = {}
        acc_distance, distance = 0, 0
        nodes_num = len(vertices)
        for n1, n2 in connections:
            distance = np.linalg.norm(self.vertices[n1] - self.vertices[n2]) / 100.0
            self.connections.append((n1, n2, distance))
            if n1 < n2:
                acc_distance += distance
                if n1 not in self.node_mass:
                    self.node_mass[n1] = 100 / acc_distance
                    #self.node_mass[n1] = 100 - acc_distance*10

        acc_distance += distance
        #self.node_mass[len(vertices)-1] = 100  / acc_distance
        self.node_mass[len(vertices) - 1] =  self.node_mass[0]


        print(self.node_mass)
        num_connections = len(self.connections)
        for idx in range( num_connections):
            cur = self.connections[idx % num_connections]
            before = self.connections[(idx-1) % num_connections]

            angle = _angle_between( self.vertices[cur[0]]-self.vertices[cur[1]],self.vertices[before[1]]-self.vertices[before[0]])
            print(f"{before[0]}-{cur[1]} : ({cur[0]}->{cur[1]} and {before[1]}->{before[0]}) angle: {angle}")
            if angle < 100: # add connection only if is supposed to be 90 degrees
                pita_length = math.sqrt(math.pow(before[2], 2) + math.pow(cur[2], 2))
                curve_connections.append((before[0], cur[1], pita_length ))

        self.connections += curve_connections
        self.connections.append((0, nodes_num-1, 0))

        #closing_distance = 1.1 * np.linalg.norm(self.vertices[1] - self.vertices[0]) / 100.0 + np.linalg.norm(self.vertices[nodes_num - 1] - self.vertices[nodes_num - 2]) / 100.0
        #self.connections.append((1, nodes_num - 2, closing_distance))

        print( self.connections )
        _dbg(f"connections: {self.connections}")
        #_dbg(f"curve_connections: {self.curve_connections}")
        _dbg(f"mass: {self.node_mass}")
        self.spring_k = 10
        self.drag_k = 0.8

        print("initial")
        #print(self.vertices)

    def spring_force(self, x1, x2, rest_distance):
        #  F = -k * (x - l0)
        distance = np.linalg.norm(x2 - x1) / 100.0
        force = self.spring_k * (distance - rest_distance)
        if distance == 0:
            return np.array([0, 0])
        force_unit_vector = (x2 - x1) / distance
        return - force * force_unit_vector

    def drag_force(self, v):
        return -1 * self.drag_k * v


    def move_node(self, node_index, error_vector):
        print(f"move_node {node_index} from {self.vertices[node_index]} direction {error_vector}")
        print(f"before move: {self.vertices[node_index]}")
        self.vertices[node_index] = self.vertices[node_index] + error_vector
        print(f"after move:   {self.vertices[node_index]}")

    def simulate(self, dt=0.5, epsilon=0.1, max_iterations=300):
        num_points = len(self.vertices)
        velocities = np.zeros_like(self.vertices)
        print("before")
        print(self.vertices)
        # while True:
        for k in range(max_iterations):
            total_movement = 0
            forces = np.zeros_like(self.vertices)
            # spring forces
            for i, j, l0 in self.connections:
                force = self.spring_force(self.vertices[i], self.vertices[j], l0)
                forces[i] = forces[i] - force
                forces[j] = forces[j] + force


            #  positions based on a=f/m   v=v0+at    x=x0+vt
            for i in range(1, num_points): # skip point 0
                drag = self.drag_force(velocities[i])
                acceleration = (forces[i] + drag) / self.node_mass[i]

                velocities[i] += acceleration * dt
                new_position = self.vertices[i] + velocities[i] * dt
                _dbg(f"m{i}: {np.linalg.norm( new_position - self.vertices[i])}")
                total_movement += new_position - self.vertices[i]
                self.vertices[i] = new_position

            _dbg(f"--------------{k}--------------")
            for n in range(num_points):
                _dbg(f"node {n} total force {_vector_to_len_angle(forces[n])} pos: {self.vertices[n]}")

            total_movement = np.linalg.norm(total_movement)
            _dbg(f"total movement: {total_movement}")
            if total_movement < epsilon:
                break

        return self.vertices

    def get_final_positions(self):
        return self.vertices





def _vector_to_len_angle(v):
    norm = np.linalg.norm(v)
    x, y = v
    radians = np.arctan2(y, x)
    degrees = np.degrees(radians)
    return norm, round(degrees, 0)


def _dbg(msg):
    if debug_enabled:
        print(msg);

def _angle_between(vector1, vector2):
    unit_vector1 = vector1 / np.linalg.norm(vector1)
    unit_vector2 = vector2 / np.linalg.norm(vector2)

    dot_product = np.dot(unit_vector1, unit_vector2)

    angle = np.arccos(dot_product)  # angle in radian
    return angle *180 /math.pi
