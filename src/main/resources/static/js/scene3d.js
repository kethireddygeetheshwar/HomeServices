/*
 * HomeServices Ultra-Premium Interactive 3D Showcases (Three.js r187)
 * Procedural PBR architectural & trade sculptures with dynamic lighting,
 * cursor-driven spring physics, click-to-spin inspection, orbiting astrolabe rings,
 * and twinkling particle constellations. Falls back seamlessly to local SVGs.
 */
(() => {
  const sceneNodes = [...document.querySelectorAll('[data-three-scene]')];
  if (!sceneNodes.length) return;

  const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  const saveData = navigator.connection && navigator.connection.saveData === true;
  const lowMemory = Number(navigator.deviceMemory || 8) <= 2;
  const lowThreads = Number(navigator.hardwareConcurrency || 8) <= 2;
  if (saveData || lowMemory || lowThreads) return;

  const threeUrl = 'https://cdn.jsdelivr.net/npm/three@0.187.0/build/three.module.js';

  const build = async () => {
    let THREE;
    try {
      THREE = await import(threeUrl);
    } catch (error) {
      console.info('HomeServices 3D module unavailable; keeping the SVG scene fallback.', error);
      return;
    }

    const palette = {
      ink: 0x111417,
      inkSoft: 0x22272c,
      obsidian: 0x0b0d0f,
      gold: 0xd9b56d,
      goldBright: 0xf4dc9f,
      goldDeep: 0x9c7634,
      cream: 0xefe6d2,
      creamLight: 0xfbf6ec,
      wood: 0xb57b47,
      woodLight: 0xd8aa72,
      blue: 0x6ea7be,
      teal: 0x5cc4ba,
      emerald: 0x6a997b,
      terracotta: 0xc26553,
      steel: 0xb4c4cb,
      copper: 0xc87d4b,
      white: 0xffffff
    };

    const stdMat = (color, roughness = 0.42, metalness = 0.18, extra = {}) =>
      new THREE.MeshStandardMaterial({ color, roughness, metalness, ...extra });

    const mats = {
      ink: stdMat(palette.ink, 0.55, 0.25),
      inkSoft: stdMat(palette.inkSoft, 0.48, 0.22),
      obsidian: stdMat(palette.obsidian, 0.28, 0.55),
      gold: stdMat(palette.gold, 0.24, 0.74),
      goldBright: stdMat(palette.goldBright, 0.2, 0.65, { emissive: 0x7a581c, emissiveIntensity: 0.22 }),
      goldDeep: stdMat(palette.goldDeep, 0.34, 0.68),
      cream: stdMat(palette.cream, 0.74, 0.04),
      creamLight: stdMat(palette.creamLight, 0.6, 0.04),
      wood: stdMat(palette.wood, 0.68, 0.05),
      woodLight: stdMat(palette.woodLight, 0.6, 0.05),
      blue: stdMat(palette.blue, 0.3, 0.36),
      teal: stdMat(palette.teal, 0.28, 0.28, { emissive: 0x1d5c56, emissiveIntensity: 0.25 }),
      emerald: stdMat(palette.emerald, 0.58, 0.04),
      terracotta: stdMat(palette.terracotta, 0.4, 0.16),
      steel: stdMat(palette.steel, 0.2, 0.85),
      copper: stdMat(palette.copper, 0.24, 0.82),
      white: stdMat(palette.white, 0.34, 0.06),
      warmWindow: new THREE.MeshStandardMaterial({
        color: 0xffe4a3,
        emissive: 0xffb94d,
        emissiveIntensity: 0.72,
        roughness: 0.18,
        metalness: 0.1
      }),
      glass: new THREE.MeshPhysicalMaterial({
        color: 0xa6cad6,
        roughness: 0.08,
        metalness: 0.05,
        transmission: 0.35,
        transparent: true,
        opacity: 0.62,
        clearcoat: 1.0,
        clearcoatRoughness: 0.08
      }),
      bubble: new THREE.MeshPhysicalMaterial({
        color: 0xdff6ff,
        roughness: 0.05,
        metalness: 0.05,
        transparent: true,
        opacity: 0.48,
        clearcoat: 1.0,
        clearcoatRoughness: 0.04
      })
    };

    const addMesh = (parent, geometry, mat, x = 0, y = 0, z = 0, rx = 0, ry = 0, rz = 0) => {
      const mesh = new THREE.Mesh(geometry, mat);
      mesh.position.set(x, y, z);
      mesh.rotation.set(rx, ry, rz);
      mesh.castShadow = true;
      mesh.receiveShadow = true;
      parent.add(mesh);
      return mesh;
    };

    const box = (parent, w, h, d, mat, x, y, z, rx = 0, ry = 0, rz = 0) =>
      addMesh(parent, new THREE.BoxGeometry(w, h, d), mat, x, y, z, rx, ry, rz);

    const cylinder = (parent, top, bottom, height, mat, x, y, z, rx = 0, ry = 0, rz = 0, segs = 32) =>
      addMesh(parent, new THREE.CylinderGeometry(top, bottom, height, segs), mat, x, y, z, rx, ry, rz);

    const sphere = (parent, radius, mat, x, y, z, scaleY = 1) => {
      const mesh = addMesh(parent, new THREE.SphereGeometry(radius, 26, 20), mat, x, y, z);
      mesh.scale.y = scaleY;
      return mesh;
    };

    const torus = (parent, radius, tubeRadius, mat, x, y, z, rx = 0, ry = 0, rz = 0) =>
      addMesh(parent, new THREE.TorusGeometry(radius, tubeRadius, 12, 72), mat, x, y, z, rx, ry, rz);

    const tube = (parent, points, mat, radius = 0.035) => {
      const curve = new THREE.CatmullRomCurve3(points.map(p => new THREE.Vector3(...p)));
      return addMesh(parent, new THREE.TubeGeometry(curve, 56, radius, 12, false), mat);
    };

    const boltShape = () => {
      const shape = new THREE.Shape();
      shape.moveTo(-0.18, 0.58);
      shape.lineTo(0.19, 0.08);
      shape.lineTo(-0.02, 0.08);
      shape.lineTo(0.16, -0.58);
      shape.lineTo(-0.36, 0.02);
      shape.lineTo(-0.11, 0.02);
      shape.closePath();
      return new THREE.ExtrudeGeometry(shape, {
        depth: 0.13,
        bevelEnabled: true,
        bevelSegments: 3,
        steps: 1,
        bevelSize: 0.04,
        bevelThickness: 0.04
      });
    };

    const addPlatform = (group) => {
      // Multi-tiered obsidian & brushed gold architectural plinth
      cylinder(group, 1.88, 1.94, 0.14, mats.obsidian, 0, -0.09, 0, 0, 0, 0, 48);
      cylinder(group, 1.78, 1.78, 0.04, mats.gold, 0, 0.0, 0, 0, 0, 0, 48);
      cylinder(group, 1.66, 1.66, 0.05, mats.ink, 0, 0.045, 0, 0, 0, 0, 48);
      torus(group, 1.92, 0.016, mats.goldBright, 0, 0.01, 0, Math.PI / 2, 0, 0);
      torus(group, 1.54, 0.01, mats.goldDeep, 0, 0.072, 0, Math.PI / 2, 0, 0);
    };

    const buildHome = (group, animatedParts) => {
      // Architectural foundation & terrace steps
      box(group, 2.88, 0.13, 2.16, mats.goldBright, 0, 0.13, 0);
      box(group, 1.0, 0.07, 0.42, mats.creamLight, 0.35, 0.11, 1.18);

      // Main residence volume + side wing
      box(group, 2.58, 1.44, 1.88, mats.cream, 0, 0.91, 0);
      box(group, 0.95, 1.05, 1.35, mats.creamLight, -1.12, 0.72, -0.1);

      // Sculpted pitched gold roofs
      const mainRoof = addMesh(group, new THREE.ConeGeometry(2.04, 1.24, 4), mats.gold, 0, 2.24, 0, 0, Math.PI / 4, 0);
      mainRoof.scale.z = 0.79;
      const wingRoof = addMesh(group, new THREE.ConeGeometry(0.95, 0.72, 4), mats.goldDeep, -1.12, 1.6, -0.1, 0, Math.PI / 4, 0);
      wingRoof.scale.z = 0.85;

      // Designer walnut front door with brass frame & handle
      box(group, 0.68, 1.12, 0.06, mats.goldBright, 0.35, 0.56, 0.95);
      box(group, 0.58, 1.04, 0.08, mats.wood, 0.35, 0.53, 0.98);
      sphere(group, 0.042, mats.goldBright, 0.54, 0.54, 1.03);

      // Warm illuminated bay windows
      const winMat = mats.warmWindow.clone();
      animatedParts.windowMat = winMat;
      [-0.68, 0.78].forEach((x, idx) => {
        const yPos = idx === 0 ? 1.14 : 1.14;
        const zPos = idx === 0 ? 0.96 : 0.96;
        box(group, 0.54, 0.54, 0.07, winMat, x, yPos, zPos);
        box(group, 0.05, 0.6, 0.09, mats.creamLight, x, yPos, zPos + 0.04);
        box(group, 0.6, 0.05, 0.09, mats.creamLight, x, yPos, zPos + 0.04);
      });

      // Architectural chimney & manicured cypress planters
      const chimney = box(group, 0.28, 0.76, 0.3, mats.terracotta, 0.9, 2.28, -0.32);
      chimney.rotation.z = -0.1;
      box(group, 0.34, 0.07, 0.36, mats.goldBright, 0.93, 2.66, -0.32, 0, 0, -0.1);

      [[-1.62, 0.35], [1.56, 0.32]].forEach(([x, z]) => {
        cylinder(group, 0.22, 0.18, 0.24, mats.inkSoft, x, 0.18, z);
        sphere(group, 0.34, mats.emerald, x, 0.52, z, 1.45);
      });
    };

    const buildElectrician = (group, animatedParts) => {
      // High-precision smart breaker cabinet
      box(group, 1.72, 1.52, 0.34, mats.inkSoft, -0.15, 1.16, 0.58);
      box(group, 1.54, 1.34, 0.08, mats.steel, -0.15, 1.16, 0.78);
      box(group, 1.38, 1.16, 0.055, mats.obsidian, -0.15, 1.16, 0.84);
      for (let i = 0; i < 3; i++) {
        box(group, 0.22, 0.36, 0.075, mats.gold, -0.58 + i * 0.43, 1.24, 0.9);
        sphere(group, 0.055, i === 1 ? mats.teal : mats.goldBright, -0.58 + i * 0.43, 0.9, 0.92);
      }

      // Levitating 3D beveled lightning emblem
      const bolt = addMesh(group, boltShape(), mats.goldBright, 1.0, 1.26, 0.42, 0, 0, -0.08);
      bolt.scale.set(0.76, 0.9, 0.85);
      animatedParts.heroFloat = bolt;
      animatedParts.heroFloatBaseY = 1.26;

      // Copper & gold conduit arcs
      tube(group, [[-1.15, 1.88, 0.38], [-1.48, 2.15, 0.18], [-1.36, 2.52, 0.16]], mats.copper, 0.048);
      tube(group, [[0.55, 1.92, 0.45], [0.75, 2.28, 0.25], [0.42, 2.55, 0.1]], mats.gold, 0.036);

      // Digital multimeter & insulated screwdriver
      box(group, 0.8, 0.52, 0.28, mats.ink, -1.04, 0.45, 0.68);
      box(group, 0.56, 0.24, 0.035, mats.teal, -1.04, 0.53, 0.84);
      sphere(group, 0.08, mats.goldBright, -0.82, 0.26, 0.84);
      cylinder(group, 0.048, 0.048, 0.52, mats.steel, 1.28, 0.52, 0.28, 0, 0, Math.PI / 3);
    };

    const buildCarpenter = (group, animatedParts) => {
      // Solid walnut joinery workbench
      box(group, 2.68, 0.2, 1.48, mats.wood, 0, 0.88, 0);
      [-1.04, 1.04].forEach(x => [-0.5, 0.5].forEach(z => box(group, 0.16, 0.82, 0.16, mats.woodLight, x, 0.42, z)));
      // Mortise timber plank & brass framing square
      box(group, 1.84, 0.24, 0.42, mats.woodLight, -0.1, 1.11, 0.02, 0, 0, -0.06);
      box(group, 0.82, 0.14, 0.4, mats.wood, -0.9, 1.27, -0.22, 0, 0.14, 0);

      // Levitating craftsman mallet
      const mallet = new THREE.Group();
      cylinder(mallet, 0.052, 0.068, 0.96, mats.woodLight, 0, 0, 0, 0, 0, -0.38);
      box(mallet, 0.62, 0.24, 0.25, mats.steel, 0.19, 0.44, 0.24, 0, 0, -0.38);
      box(mallet, 0.26, 0.23, 0.31, mats.goldBright, -0.15, 0.41, 0.19, 0, 0, -0.38);
      mallet.position.set(0.68, 1.54, 0.32);
      group.add(mallet);
      animatedParts.heroFloat = mallet;
      animatedParts.heroFloatBaseY = 1.54;
    };

    const buildPainter = (group, animatedParts) => {
      // Architectural gallery panel with champagne & slate swatches
      box(group, 2.24, 1.82, 0.14, mats.creamLight, 0, 1.1, -0.12);
      box(group, 0.92, 0.76, 0.08, mats.goldBright, -0.5, 1.28, -0.01);
      box(group, 0.92, 0.76, 0.08, mats.blue, 0.5, 1.28, -0.01);

      // Levitating paint roller assembly
      const rollerGroup = new THREE.Group();
      cylinder(rollerGroup, 0.09, 0.09, 1.15, mats.woodLight, 0, -0.25, 0, 0, 0, -0.16);
      cylinder(rollerGroup, 0.21, 0.21, 0.62, mats.cream, -0.08, 0.38, 0, 0, 0, Math.PI / 2);
      cylinder(rollerGroup, 0.215, 0.215, 0.34, mats.goldBright, -0.08, 0.38, 0, 0, 0, Math.PI / 2);
      rollerGroup.position.set(0.95, 1.35, 0.54);
      group.add(rollerGroup);
      animatedParts.heroFloat = rollerGroup;
      animatedParts.heroFloatBaseY = 1.35;

      // Brass-rimmed paint vessel & palette block
      cylinder(group, 0.35, 0.42, 0.62, mats.terracotta, -1.05, 0.39, 0.65);
      cylinder(group, 0.36, 0.36, 0.08, mats.goldBright, -1.05, 0.72, 0.65);
      box(group, 0.44, 0.28, 0.34, mats.gold, 0.12, 0.34, 0.72);
    };

    const buildMechanic = (group, animatedParts) => {
      // Sculpted coupe body with clearcoat glass cabin
      box(group, 2.58, 0.62, 1.26, mats.blue, 0, 0.86, 0);
      box(group, 1.36, 0.62, 1.14, mats.inkSoft, -0.2, 1.38, -0.03);
      box(group, 1.18, 0.44, 0.05, mats.glass, -0.2, 1.42, 0.56);
      box(group, 0.72, 0.33, 0.05, mats.glass, 0.55, 1.43, 0.56);

      // Gold-spoked alloy wheels
      animatedParts.wheels = [];
      [-0.82, 0.82].forEach(x => {
        const wheel = new THREE.Group();
        cylinder(wheel, 0.4, 0.4, 0.22, mats.obsidian, 0, 0, 0, Math.PI / 2, 0, 0);
        cylinder(wheel, 0.21, 0.21, 0.235, mats.goldBright, 0, 0, 0, Math.PI / 2, 0, 0);
        wheel.position.set(x, 0.45, 0.67);
        group.add(wheel);
        animatedParts.wheels.push(wheel);
      });

      // Floating precision torque wrench & gear ring
      const tool = new THREE.Group();
      cylinder(tool, 0.1, 0.1, 0.82, mats.steel, 0, -0.22, 0, 0, 0, 0.85);
      torus(tool, 0.24, 0.048, mats.goldBright, 0.04, 0.28, 0, 0, 0, 0.35);
      tool.position.set(1.2, 1.42, 0.7);
      group.add(tool);
      animatedParts.heroFloat = tool;
      animatedParts.heroFloatBaseY = 1.42;
    };

    const buildPlumber = (group, animatedParts) => {
      // Brushed chrome & copper manifold pipework
      tube(group, [[-1.25, 1.85, 0.35], [-1.25, 2.28, 0.35], [-0.35, 2.28, 0.35], [-0.35, 1.6, 0.35], [0.62, 1.6, 0.35]], mats.steel, 0.098);
      tube(group, [[-1.25, 2.28, 0.35], [-1.25, 2.56, 0.35], [-0.65, 2.56, 0.35]], mats.copper, 0.072);

      // Rotating golden gate valve
      const valve = torus(group, 0.25, 0.042, mats.goldBright, -0.65, 2.56, 0.35, Math.PI / 2, 0, 0);
      animatedParts.spinZ = valve;
      cylinder(group, 0.07, 0.07, 0.56, mats.gold, -0.65, 2.24, 0.35);

      // Crystal water droplets
      const drop1 = sphere(group, 0.125, mats.teal, 0.93, 0.72, 0.4, 1.38);
      sphere(group, 0.088, mats.blue, 1.18, 0.38, 0.55, 1.28);
      animatedParts.heroFloat = drop1;
      animatedParts.heroFloatBaseY = 0.72;

      box(group, 0.74, 0.18, 0.76, mats.cream, 0.45, 0.24, -0.2);
      cylinder(group, 0.05, 0.05, 0.56, mats.steel, 1.18, 1.04, 0.55, 0, 0, -0.8);
    };

    const buildCleaning = (group, animatedParts) => {
      // Teal & brushed-gold cleaning caddy & atomizer bottle
      cylinder(group, 0.54, 0.68, 0.84, mats.teal, -0.55, 0.47, 0.22);
      cylinder(group, 0.52, 0.52, 0.1, mats.goldBright, -0.55, 0.93, 0.22);
      box(group, 0.46, 0.08, 0.3, mats.creamLight, -0.55, 1.18, 0.22);

      box(group, 0.56, 0.74, 0.36, mats.creamLight, 0.65, 0.78, 0.4);
      cylinder(group, 0.13, 0.13, 0.26, mats.teal, 0.65, 1.25, 0.4);
      box(group, 0.38, 0.1, 0.16, mats.goldBright, 0.79, 1.39, 0.4);
      cylinder(group, 0.045, 0.045, 0.4, mats.inkSoft, 0.98, 1.53, 0.4, 0, 0, Math.PI / 2);

      // Iridescent floating soap bubbles
      const bubbleGroup = new THREE.Group();
      [
        [-1.15, 1.65, 0.55, 0.14],
        [1.18, 1.95, -0.15, 0.12],
        [0.15, 2.28, 0.65, 0.095],
        [1.4, 0.75, 0.2, 0.085],
        [-0.35, 2.05, -0.45, 0.11]
      ].forEach(([x, y, z, r]) => sphere(bubbleGroup, r, mats.bubble, x, y, z, 1.04));
      group.add(bubbleGroup);
      animatedParts.heroFloat = bubbleGroup;
      animatedParts.heroFloatBaseY = 0;
    };

    const builders = {
      home: buildHome,
      electrician: buildElectrician,
      carpenter: buildCarpenter,
      painter: buildPainter,
      mechanic: buildMechanic,
      plumber: buildPlumber,
      cleaning: buildCleaning
    };

    const createScene = (node) => {
      const kind = String(node.dataset.category || node.dataset.threeScene || 'home').toLowerCase();
      const key = Object.keys(builders).find(name => kind.includes(name)) || 'home';
      let renderer;
      try {
        renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true, powerPreference: 'high-performance' });
      } catch (error) {
        console.info('WebGL is unavailable; keeping the SVG scene fallback.', error);
        return null;
      }

      const isHero = node.classList.contains('home-3d-art');
      renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, window.matchMedia('(max-width: 760px)').matches ? 1.25 : 1.75));
      renderer.setClearColor(0x000000, 0);
      renderer.outputColorSpace = THREE.SRGBColorSpace;
      renderer.toneMapping = THREE.ACESFilmicToneMapping;
      renderer.toneMappingExposure = 1.18;
      renderer.domElement.className = 'scene3d-canvas';
      renderer.domElement.setAttribute('aria-hidden', 'true');
      renderer.domElement.tabIndex = -1;
      node.appendChild(renderer.domElement);

      const scene = new THREE.Scene();
      const width = Math.max(1, node.clientWidth);
      const height = Math.max(1, node.clientHeight);
      const camera = new THREE.PerspectiveCamera(33, width / height, 0.1, 60);
      camera.position.set(5.1, 3.65, 6.85);
      camera.lookAt(0, 1.08, 0);

      // Studio Three-Point + Dynamic Interactive Rim Lighting
      scene.add(new THREE.HemisphereLight(0xfff5de, 0x453d32, 2.15));
      const keyLight = new THREE.DirectionalLight(0xffe8ba, 3.2);
      keyLight.position.set(4.5, 7.2, 5.5);
      scene.add(keyLight);

      const fillLight = new THREE.PointLight(0xe5bd70, 32, 14, 1.8);
      fillLight.position.set(-3.4, 2.5, 3.8);
      scene.add(fillLight);

      const rimLight = new THREE.PointLight(0x7cc4d4, 20, 12, 1.8);
      rimLight.position.set(2.9, 2.7, -3.5);
      scene.add(rimLight);

      const model = new THREE.Group();
      scene.add(model);
      addPlatform(model);

      const animatedParts = {};
      builders[key](model, animatedParts);

      // Concentric astrolabe orbital rings
      const orbit = torus(model, 2.34, 0.013, mats.goldBright, 0, 1.06, 0, Math.PI / 2.7, 0.12, -0.18);
      const orbitEcho = torus(model, 2.04, 0.009, mats.gold, 0, 1.16, 0, Math.PI / 2.15, -0.38, 0.34);
      const orbitOuter = isHero
        ? torus(model, 2.58, 0.007, mats.blue, 0, 0.98, 0, Math.PI / 3.1, 0.42, 0.22)
        : null;

      // Floating stardust & orbital nodes
      const accents = new THREE.Group();
      model.add(accents);
      [[-1.92, 2.42, 0.25], [1.78, 2.68, -0.35], [1.92, 0.82, 0.65], [-1.78, 0.62, -0.55]].forEach((pos, index) => {
        const dot = sphere(accents, index % 2 ? 0.062 : 0.088, index % 2 ? mats.blue : mats.goldBright, ...pos);
        dot.userData.phase = index * 1.3;
        dot.userData.baseY = pos[1];
      });

      const sparkleCount = isHero ? 22 : 12;
      for (let index = 0; index < sparkleCount; index += 1) {
        const angle = (index / sparkleCount) * Math.PI * 2;
        const radius = (isHero ? 2.42 : 2.15) + (index % 3) * 0.14;
        const x = Math.cos(angle) * radius;
        const y = 0.58 + (index % 5) * 0.44;
        const z = Math.sin(angle) * radius * 0.76;
        const sparkle = sphere(
          accents,
          index % 4 === 0 ? 0.044 : 0.025,
          index % 3 === 0 ? mats.goldBright : (index % 2 === 0 ? mats.teal : mats.blue),
          x, y, z
        );
        sparkle.userData.phase = index * 0.61;
        sparkle.userData.baseY = y;
        sparkle.userData.twinkle = true;
      }

      const fallbackSvg = node.querySelector('.scene-svg');
      const resize = () => {
        const w = Math.max(1, node.clientWidth);
        const h = Math.max(1, node.clientHeight);
        camera.aspect = w / h;
        camera.updateProjectionMatrix();
        renderer.setSize(w, h, false);
      };
      resize();
      if (fallbackSvg) fallbackSvg.style.opacity = '0';
      node.classList.add('has-three-scene');

      let raf = 0;
      let visible = false;
      let targetRotY = 0;
      let targetRotX = 0;
      let spinImpulse = 0;
      const clock = new THREE.Clock();

      const renderFrame = () => {
        const elapsed = clock.getElapsedTime();

        // Decay celebratory spin impulse smoothly
        spinImpulse *= 0.92;
        if (Math.abs(spinImpulse) < 0.0005) spinImpulse = 0;

        const idleSway = reducedMotion ? 0 : Math.sin(elapsed * 0.55) * 0.09;
        model.rotation.y += ((targetRotY + idleSway) - model.rotation.y) * 0.055 + spinImpulse;
        model.rotation.x += (targetRotX - model.rotation.x) * 0.055;
        model.position.y = reducedMotion ? 0 : Math.sin(elapsed * 0.85) * 0.06;

        // Orbiting astrolabe rings
        if (!reducedMotion) {
          orbit.rotation.z = elapsed * 0.11;
          orbitEcho.rotation.z = -elapsed * 0.075;
          if (orbitOuter) orbitOuter.rotation.z = elapsed * 0.045;
        }

        // Trade-specific kinetic parts
        if (!reducedMotion) {
          if (animatedParts.heroFloat) {
            animatedParts.heroFloat.position.y = animatedParts.heroFloatBaseY + Math.sin(elapsed * 1.7) * 0.08;
            animatedParts.heroFloat.rotation.y = Math.sin(elapsed * 0.9) * 0.18;
          }
          if (animatedParts.windowMat) {
            animatedParts.windowMat.emissiveIntensity = 0.62 + Math.sin(elapsed * 1.8) * 0.18;
          }
          if (animatedParts.wheels) {
            animatedParts.wheels.forEach(w => { w.rotation.z -= 0.025; });
          }
          if (animatedParts.spinZ) {
            animatedParts.spinZ.rotation.y = elapsed * 0.8;
          }
        }

        // Dynamic accent lights & stardust constellation
        fillLight.position.x = -3.2 + Math.sin(elapsed * 0.6) * 0.9;
        accents.children.forEach(dot => {
          const phase = dot.userData.phase;
          const motion = reducedMotion ? 0 : Math.sin(elapsed * 1.35 + phase);
          dot.position.y = dot.userData.baseY + motion * (dot.userData.twinkle ? 0.085 : 0.06);
          if (dot.userData.twinkle) {
            const scale = 0.7 + (Math.sin(elapsed * 2.2 + phase) + 1) * 0.26;
            dot.scale.setScalar(scale);
          }
        });

        renderer.render(scene, camera);
        if (!reducedMotion && visible) raf = window.requestAnimationFrame(renderFrame);
      };

      const start = () => {
        if (visible) return;
        visible = true;
        clock.start();
        if (reducedMotion) renderFrame();
        else raf = window.requestAnimationFrame(renderFrame);
      };
      const stop = () => {
        visible = false;
        if (raf) window.cancelAnimationFrame(raf);
      };

      if ('ResizeObserver' in window) new ResizeObserver(resize).observe(node);
      else window.addEventListener('resize', resize, { passive: true });

      const parentStage = node.closest('.hero-art, .directory-showcase, .service-detail-stage') || node;
      parentStage.addEventListener('pointermove', event => {
        if (reducedMotion || !window.matchMedia('(hover: hover) and (pointer: fine)').matches) return;
        const rect = parentStage.getBoundingClientRect();
        const pointerX = (event.clientX - rect.left) / Math.max(1, rect.width) - 0.5;
        const pointerY = (event.clientY - rect.top) / Math.max(1, rect.height) - 0.5;
        targetRotY = pointerX * 0.42;
        targetRotX = -pointerY * 0.16;
        fillLight.position.x = -2.5 + pointerX * 3.5;
        fillLight.position.y = 2.6 - pointerY * 2.0;
      }, { passive: true });

      parentStage.addEventListener('pointerleave', () => {
        targetRotY = 0;
        targetRotX = 0;
      }, { passive: true });

      // Click / tap gives the 3D sculpture a smooth celebratory spin impulse
      parentStage.addEventListener('click', (event) => {
        if (reducedMotion || event.target.closest('a, button, input, select, textarea')) return;
        spinImpulse = 0.24;
      });

      renderer.domElement.addEventListener('webglcontextlost', event => {
        event.preventDefault();
        stop();
        renderer.domElement.remove();
        renderer.dispose();
        if (fallbackSvg) fallbackSvg.style.opacity = '';
        node.classList.remove('has-three-scene');
      }, { once: true });

      return {
        start,
        stop,
        dispose: () => {
          stop();
          renderer.dispose();
          renderer.domElement.remove();
          if (fallbackSvg) fallbackSvg.style.opacity = '';
          node.classList.remove('has-three-scene');
        }
      };
    };

    const scenePairs = sceneNodes
      .map(node => ({ node, instance: createScene(node) }))
      .filter(pair => pair.instance);
    if (!scenePairs.length) return;
    const scenes = scenePairs.map(pair => pair.instance);

    if ('IntersectionObserver' in window) {
      const sceneByNode = new Map(scenePairs.map(pair => [pair.node, pair.instance]));
      const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
          const instance = sceneByNode.get(entry.target);
          if (!instance) return;
          if (entry.isIntersecting) instance.start();
          else instance.stop();
        });
      }, { threshold: 0.05, rootMargin: '140px 0px' });
      sceneByNode.forEach((instance, node) => observer.observe(node));
      document.addEventListener('visibilitychange', () => {
        if (document.hidden) scenes.forEach(instance => instance.stop());
        else scenes.forEach(instance => instance.start());
      });
    } else {
      scenes.forEach(instance => instance.start());
    }
  };

  build();
})();
