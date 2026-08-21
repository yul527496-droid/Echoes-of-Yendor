extends Node3D

const PlayerScript = preload("res://scripts/player.gd")

var stone_material: StandardMaterial3D
var dark_stone_material: StandardMaterial3D
var metal_material: StandardMaterial3D
var grave_material: StandardMaterial3D

func _ready() -> void:
	_build_materials()
	_build_environment()
	_build_tomb()
	_spawn_player()
	_build_hud()

func _build_materials() -> void:
	stone_material = _material(Color(0.19, 0.195, 0.21), 0.95)
	dark_stone_material = _material(Color(0.075, 0.08, 0.095), 1.0)
	metal_material = _material(Color(0.11, 0.115, 0.125), 0.42, 0.5)
	grave_material = _material(Color(0.115, 0.12, 0.13), 1.0)

func _material(color: Color, roughness: float, metallic := 0.0) -> StandardMaterial3D:
	var mat := StandardMaterial3D.new()
	mat.albedo_color = color
	mat.roughness = roughness
	mat.metallic = metallic
	return mat

func _build_environment() -> void:
	var world_env := WorldEnvironment.new()
	var env := Environment.new()
	env.background_mode = Environment.BG_COLOR
	env.background_color = Color(0.007, 0.009, 0.014)
	env.ambient_light_source = Environment.AMBIENT_SOURCE_COLOR
	env.ambient_light_color = Color(0.16, 0.18, 0.23)
	env.ambient_light_energy = 0.42
	env.fog_enabled = true
	env.fog_light_color = Color(0.075, 0.085, 0.11)
	env.fog_light_energy = 0.45
	env.fog_density = 0.018
	env.fog_height = 0.0
	env.fog_height_density = 0.35
	world_env.environment = env
	add_child(world_env)

	var moon := DirectionalLight3D.new()
	moon.rotation_degrees = Vector3(-52.0, -28.0, 0.0)
	moon.light_color = Color(0.48, 0.56, 0.75)
	moon.light_energy = 0.9
	moon.shadow_enabled = true
	add_child(moon)

func _build_tomb() -> void:
	_add_box("Floor", Vector3(22.0, 0.6, 42.0), Vector3(0.0, -0.3, -8.0), stone_material, true)
	_add_box("LeftWall", Vector3(1.0, 7.0, 42.0), Vector3(-10.5, 3.5, -8.0), dark_stone_material, true)
	_add_box("RightWall", Vector3(1.0, 7.0, 42.0), Vector3(10.5, 3.5, -8.0), dark_stone_material, true)
	_add_box("RearWall", Vector3(22.0, 7.0, 1.0), Vector3(0.0, 3.5, 12.5), dark_stone_material, true)

	for z in [-2.0, -9.0, -16.0, -23.0]:
		_add_box("PillarL", Vector3(1.3, 6.0, 1.3), Vector3(-7.2, 3.0, z), stone_material, true)
		_add_box("PillarR", Vector3(1.3, 6.0, 1.3), Vector3(7.2, 3.0, z), stone_material, true)
		_add_box("Lintel", Vector3(15.7, 1.0, 1.3), Vector3(0.0, 6.0, z), stone_material, true)

	for row in range(4):
		for side in [-1.0, 1.0]:
			for i in range(4):
				var gx: float = side * (4.0 + float(i % 2) * 1.45)
				var gz: float = 7.0 - float(row) * 4.0 - float(i / 2) * 1.2
				_add_box("NamelessGrave", Vector3(0.75, 1.55, 0.22), Vector3(gx, 0.78, gz), grave_material, true)

	_add_box("SarcophagusBase", Vector3(2.2, 0.7, 4.1), Vector3(0.0, 0.35, 7.8), dark_stone_material, true)
	_add_box("SarcophagusLid", Vector3(2.0, 0.22, 3.8), Vector3(0.35, 0.92, 8.0), stone_material, true, Vector3(0.0, 0.0, -7.0))

	_add_box("GateHeader", Vector3(8.6, 1.2, 1.2), Vector3(0.0, 5.7, -28.0), stone_material, true)
	_add_box("GateSideL", Vector3(1.3, 5.7, 1.2), Vector3(-4.2, 2.85, -28.0), stone_material, true)
	_add_box("GateSideR", Vector3(1.3, 5.7, 1.2), Vector3(4.2, 2.85, -28.0), stone_material, true)
	for x in range(-3, 4):
		_add_box("GateBar", Vector3(0.14, 4.7, 0.18), Vector3(float(x), 2.35, -27.55), metal_material, true)

	for z in [4.0, -6.0, -16.0, -25.0]:
		_add_ghost_light(Vector3(-5.7, 2.5, z))
		_add_ghost_light(Vector3(5.7, 2.5, z))

func _add_box(node_name: String, size: Vector3, position: Vector3, material: Material, with_collision: bool, rotation_deg := Vector3.ZERO) -> Node3D:
	var root := StaticBody3D.new() if with_collision else Node3D.new()
	root.name = node_name
	root.position = position
	root.rotation_degrees = rotation_deg
	add_child(root)

	var mesh_instance := MeshInstance3D.new()
	var mesh := BoxMesh.new()
	mesh.size = size
	mesh_instance.mesh = mesh
	mesh_instance.material_override = material
	root.add_child(mesh_instance)

	if with_collision:
		var shape_node := CollisionShape3D.new()
		var shape := BoxShape3D.new()
		shape.size = size
		shape_node.shape = shape
		root.add_child(shape_node)
	return root

func _add_ghost_light(position: Vector3) -> void:
	var light := OmniLight3D.new()
	light.position = position
	light.light_color = Color(0.28, 0.48, 0.72)
	light.light_energy = 2.2
	light.omni_range = 6.5
	light.shadow_enabled = true
	add_child(light)

	var ember := MeshInstance3D.new()
	var sphere := SphereMesh.new()
	sphere.radius = 0.08
	sphere.height = 0.16
	ember.mesh = sphere
	ember.position = position
	var glow := StandardMaterial3D.new()
	glow.albedo_color = Color(0.35, 0.62, 0.9)
	glow.emission_enabled = true
	glow.emission = Color(0.25, 0.55, 1.0)
	glow.emission_energy_multiplier = 4.0
	ember.material_override = glow
	add_child(ember)

func _spawn_player() -> void:
	var player := CharacterBody3D.new()
	player.set_script(PlayerScript)
	player.position = Vector3(0.0, 0.02, 5.0)
	add_child(player)

func _build_hud() -> void:
	var canvas := CanvasLayer.new()
	add_child(canvas)

	var title := Label.new()
	title.text = "ELDRATH"
	title.position = Vector2(34, 28)
	title.add_theme_font_size_override("font_size", 30)
	canvas.add_child(title)

	var subtitle := Label.new()
	subtitle.text = "THE NAMELESS TOMB  ·  PROTOTYPE 0.0.1"
	subtitle.position = Vector2(36, 66)
	subtitle.modulate = Color(0.62, 0.67, 0.76)
	subtitle.add_theme_font_size_override("font_size", 13)
	canvas.add_child(subtitle)

	var controls := Label.new()
	controls.text = "WASD  移动    SHIFT  奔跑    SPACE  跳跃    鼠标  视角    ESC  释放鼠标"
	controls.position = Vector2(34, 665)
	controls.modulate = Color(0.7, 0.72, 0.76)
	controls.add_theme_font_size_override("font_size", 14)
	canvas.add_child(controls)

	var objective := Label.new()
	objective.text = "目标：走向墓窟尽头的铁门"
	objective.position = Vector2(950, 36)
	objective.modulate = Color(0.78, 0.8, 0.84)
	objective.add_theme_font_size_override("font_size", 14)
	canvas.add_child(objective)
