// HomeServices interactive UI: accessible navigation, 3D service illustrations and subtle motion.
// All service artwork is generated from a fixed local SVG catalogue; no external image service is required.
document.addEventListener('DOMContentLoaded', () => {
  if (document.documentElement.dataset.homeServicesUiInitialized === 'true') return;
  document.documentElement.dataset.homeServicesUiInitialized = 'true';

  const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  const categoryKey = (value) => {
    const text = String(value || '').trim().toLowerCase();
    if (text.includes('electric')) return 'electrician';
    if (text.includes('carpent')) return 'carpenter';
    if (text.includes('paint')) return 'painter';
    if (text.includes('mechan') || text.includes('auto')) return 'mechanic';
    if (text.includes('plumb')) return 'plumber';
    if (text.includes('clean')) return 'cleaning';
    return 'home';
  };

  const art = {
    home: {
      label: 'Home care', accent: '#dfbd79',
      svg: `<svg viewBox="0 0 420 300" class="scene-svg" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <defs><linearGradient id="homeWall" x2="1" y2="1"><stop stop-color="#f4dfb4"/><stop offset="1" stop-color="#bd9352"/></linearGradient><linearGradient id="homeRoof" x2="1"><stop stop-color="#e7c27b"/><stop offset="1" stop-color="#926c36"/></linearGradient><filter id="homeShadow"><feGaussianBlur stdDeviation="8"/></filter></defs>
        <ellipse cx="214" cy="260" rx="142" ry="22" fill="#000" opacity=".28" filter="url(#homeShadow)"/>
        <path d="M89 142 211 51l123 91v105H89Z" fill="url(#homeWall)" stroke="#f8e9c9" stroke-width="3"/><path d="m66 145 145-111 145 111-19 17L211 59 85 162Z" fill="url(#homeRoof)" stroke="#f5d99e" stroke-width="3"/>
        <path d="M178 248v-82q0-10 10-10h48q10 0 10 10v82" fill="#47372b" stroke="#f7e4bd" stroke-width="4"/><circle cx="232" cy="201" r="4" fill="#e4bd70"/>
        <rect x="112" y="151" width="45" height="43" rx="4" fill="#253c48" stroke="#fff0cf" stroke-width="5"/><path d="M134 151v43m-22-21h45" stroke="#f7e7c8" stroke-width="4"/>
        <rect x="266" y="151" width="45" height="43" rx="4" fill="#253c48" stroke="#fff0cf" stroke-width="5"/><path d="M288 151v43m-22-21h45" stroke="#f7e7c8" stroke-width="4"/>
        <path d="M83 245q-4-42 25-55 29 14 25 55Z" fill="#55745b"/><path d="M287 245q-4-42 25-55 29 14 25 55Z" fill="#55745b"/><path d="M95 247h38m184 0h38" stroke="#c7a46b" stroke-width="7" stroke-linecap="round"/>
        <path d="m338 73 5 12 13 5-13 5-5 13-5-13-13-5 13-5Z" fill="#fff0cb"/><circle cx="92" cy="87" r="7" fill="#e7c47e"/><circle cx="359" cy="180" r="5" fill="#e7c47e"/>
      </svg>`
    },
    electrician: {
      label: 'Electrician at work', accent: '#f0c45f',
      svg: `<svg viewBox="0 0 420 300" class="scene-svg" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <defs><linearGradient id="elecJacket" x2="1" y2="1"><stop stop-color="#e8b84f"/><stop offset="1" stop-color="#a76e28"/></linearGradient><filter id="elecGlow"><feGaussianBlur stdDeviation="5"/></filter></defs>
        <ellipse cx="210" cy="265" rx="135" ry="18" fill="#000" opacity=".3"/>
        <circle cx="309" cy="100" r="55" fill="#e6bd69" opacity=".12" filter="url(#elecGlow)"/><path d="m305 39-31 62h27l-15 56 52-75h-29l20-43Z" fill="#f8cd65" stroke="#fff0bf" stroke-width="3"/><path d="m305 39-31 62h27l-15 56 52-75h-29l20-43Z" fill="#ffd56d" opacity=".28"/>
        <path d="M148 248q-1-55 18-88 20-22 53-18 42 4 56 46l8 60Z" fill="url(#elecJacket)" stroke="#f6d68c" stroke-width="3"/><path d="m160 187-47 44q-9 9-1 17 8 7 17-1l56-40" fill="#c98f47" stroke="#f2ce8c" stroke-width="5"/><path d="m254 177 37 38q8 8 17 0 8-9-1-17l-43-43" fill="#c98f47" stroke="#f2ce8c" stroke-width="5"/>
        <path d="M173 116q3-40 44-43 45 3 47 46v23h-91Z" fill="#e7b77c" stroke="#fff0cf" stroke-width="3"/><path d="M163 113q10-47 55-47 41 0 55 45l-10 7h-89Z" fill="#e6b84f" stroke="#fff0c5" stroke-width="4"/><path d="M157 112h120" stroke="#fff0c5" stroke-width="8" stroke-linecap="round"/><path d="M194 132q19 16 39 0" fill="none" stroke="#8a5a37" stroke-width="4" stroke-linecap="round"/>
        <rect x="82" y="215" width="56" height="38" rx="7" fill="#303a42" stroke="#e8c57b" stroke-width="3"/><rect x="95" y="225" width="30" height="8" rx="3" fill="#e7bd64"/><circle cx="110" cy="242" r="4" fill="#72d1be"/><path d="m286 224 24 23m-22-34 34 34" stroke="#d7d2c9" stroke-width="8" stroke-linecap="round"/><path d="m311 241 8-8 13 13-8 8Z" fill="#c28b40"/>
        <circle cx="93" cy="85" r="5" fill="#f4d58e"/><circle cx="346" cy="203" r="7" fill="#f4d58e"/><path d="m78 128 4 10 10 4-10 4-4 10-4-10-10-4 10-4Z" fill="#f7d475"/>
      </svg>`
    },
    carpenter: {
      label: 'Carpenter at work', accent: '#d6a36a',
      svg: `<svg viewBox="0 0 420 300" class="scene-svg" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <defs><linearGradient id="wood" x2="1" y2="1"><stop stop-color="#e8bc7b"/><stop offset="1" stop-color="#9e6238"/></linearGradient><linearGradient id="carpShirt" x2="1" y2="1"><stop stop-color="#d6a36a"/><stop offset="1" stop-color="#8d5638"/></linearGradient></defs>
        <ellipse cx="207" cy="263" rx="145" ry="18" fill="#000" opacity=".28"/>
        <path d="M67 207 280 176l71 48-213 39Z" fill="url(#wood)" stroke="#f2d6a3" stroke-width="3"/><path d="m86 211 206-31m-174 57 204-35" stroke="#8e5835" stroke-width="3" opacity=".6"/><path d="M109 249v12m202-47v18" stroke="#70482f" stroke-width="10" stroke-linecap="round"/>
        <path d="M162 248q-2-50 16-79 20-17 51-14 30 4 45 36l8 57Z" fill="url(#carpShirt)" stroke="#f0c68e" stroke-width="3"/><circle cx="220" cy="115" r="42" fill="#d99f70" stroke="#f4d6b2" stroke-width="3"/><path d="M176 105q4-42 45-43 42 1 49 44l-16-8-14-19-27 17-28 5Z" fill="#b37b43"/><path d="M181 82q39-42 79 0" fill="none" stroke="#e7b96c" stroke-width="12" stroke-linecap="round"/><path d="m196 124 9 4m28-4 9 4m-30 21q11 8 22 0" fill="none" stroke="#704532" stroke-width="4" stroke-linecap="round"/>
        <path d="m262 163 39-66q7-10 17-4 10 7 3 18l-43 67" fill="#d9a768" stroke="#f5dcaf" stroke-width="4"/><path d="m291 95 15-19 25 17-15 20Z" fill="#6f4935" stroke="#e8c38b" stroke-width="3"/><path d="m305 75 10-15 29 21-10 15Z" fill="#c68f51"/>
        <path d="M82 136h40m-20-20v40" stroke="#dfb36f" stroke-width="4" stroke-linecap="round"/><circle cx="342" cy="91" r="16" fill="#a97747" opacity=".5"/><path d="m342 70 5 13 13 5-13 5-5 13-5-13-13-5 13-5Z" fill="#f2d39c"/>
      </svg>`
    },
    painter: {
      label: 'Painter at work', accent: '#9cb6c7',
      svg: `<svg viewBox="0 0 420 300" class="scene-svg" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <defs><linearGradient id="paintWall" x2="1" y2="1"><stop stop-color="#e8eef0"/><stop offset="1" stop-color="#8da8bb"/></linearGradient><linearGradient id="paintSuit" x2="1" y2="1"><stop stop-color="#d4a45f"/><stop offset="1" stop-color="#996b3e"/></linearGradient></defs>
        <ellipse cx="205" cy="263" rx="140" ry="17" fill="#000" opacity=".27"/><path d="M62 65q0-12 12-12h205q12 0 12 12v162H62Z" fill="url(#paintWall)" stroke="#f5e8d0" stroke-width="4"/><path d="M77 77h117v137H77Z" fill="#d9b77d" opacity=".75"/><path d="M88 91h96v104H88Z" fill="#f4dfb9"/>
        <path d="M184 244q-2-45 17-70 17-17 44-14 31 4 44 35l6 49Z" fill="url(#paintSuit)" stroke="#f3d3a0" stroke-width="3"/><circle cx="231" cy="126" r="38" fill="#d99f73" stroke="#f4d4b0" stroke-width="3"/><path d="M192 118q8-40 42-41 35 2 43 37l-16-3-14-14-21 12-27 3Z" fill="#c99753"/><path d="M196 83q35-24 70 2" fill="none" stroke="#e6c17e" stroke-width="12" stroke-linecap="round"/>
        <path d="m262 162 40-59" stroke="#d4a15e" stroke-width="9" stroke-linecap="round"/><path d="m292 97 14-43q2-7 10-5l15 5q7 2 4 9l-14 44Z" fill="#ecf0ed" stroke="#d4c7ac" stroke-width="3"/><path d="m298 78 28 9" stroke="#c6a15f" stroke-width="7" stroke-linecap="round"/><path d="M312 47q18 6 7 23" fill="none" stroke="#d7c8a8" stroke-width="3"/>
        <path d="M88 219h58l-8 31H97Z" fill="#b85e4b" stroke="#f1d4a7" stroke-width="3"/><path d="M96 214h44" stroke="#f4dcaa" stroke-width="5" stroke-linecap="round"/><path d="M111 229q10-16 20 0" fill="none" stroke="#e7b96e" stroke-width="3"/><circle cx="342" cy="100" r="8" fill="#d9b56d"/><circle cx="357" cy="129" r="5" fill="#d9b56d"/><path d="m344 70 4 9 9 4-9 4-4 9-4-9-9-4 9-4Z" fill="#fff0c8"/>
      </svg>`
    },
    mechanic: {
      label: 'Mechanic at work', accent: '#94b4c2',
      svg: `<svg viewBox="0 0 420 300" class="scene-svg" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <defs><linearGradient id="carBody" x2="1" y2="1"><stop stop-color="#9db8c6"/><stop offset="1" stop-color="#425b69"/></linearGradient><linearGradient id="mechTop" x2="1" y2="1"><stop stop-color="#d5a464"/><stop offset="1" stop-color="#875a39"/></linearGradient></defs>
        <ellipse cx="207" cy="260" rx="150" ry="18" fill="#000" opacity=".3"/><path d="M73 180 102 126q8-15 25-15h147q17 0 27 14l38 55 18 10v47H56v-42Z" fill="url(#carBody)" stroke="#d9e8ec" stroke-width="4"/><path d="m117 126-25 47h92v-47Zm81 0v47h111l-31-43q-4-4-12-4Z" fill="#253844" stroke="#e3d9c2" stroke-width="4"/><path d="M61 207h332" stroke="#e5c17f" stroke-width="5"/><circle cx="122" cy="235" r="27" fill="#1c252a" stroke="#d9b56d" stroke-width="7"/><circle cx="122" cy="235" r="10" fill="#a7b8bc"/><circle cx="303" cy="235" r="27" fill="#1c252a" stroke="#d9b56d" stroke-width="7"/><circle cx="303" cy="235" r="10" fill="#a7b8bc"/>
        <path d="M232 100q18-27 43-11 17 12 5 32l-15 16-39-13Z" fill="#d9a16e" stroke="#f3d3ae" stroke-width="3"/><path d="M219 139q25-24 54-12 20 10 25 42l-4 36h-91l-1-37Z" fill="url(#mechTop)" stroke="#f2d0a0" stroke-width="3"/><path d="m222 149-43 39q-8 8-1 16 9 7 17-1l46-38" fill="#d59c67" stroke="#f1d1a0" stroke-width="5"/><path d="m288 151 31 28q9 8 17-1 7-9-2-17l-32-28" fill="#d59c67" stroke="#f1d1a0" stroke-width="5"/>
        <path d="m341 68 13 14-32 32-13-14Z" fill="#d7dce0" stroke="#f2d39c" stroke-width="3"/><path d="m323 113-10 19m0-19 19 10" stroke="#d7dce0" stroke-width="7" stroke-linecap="round"/><circle cx="356" cy="155" r="22" fill="none" stroke="#d9b56d" stroke-width="5"/><circle cx="356" cy="155" r="6" fill="#d9b56d"/>
      </svg>`
    },
    plumber: {
      label: 'Plumber at work', accent: '#79b8c1',
      svg: `<svg viewBox="0 0 420 300" class="scene-svg" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <defs><linearGradient id="pipe" x2="1" y2="1"><stop stop-color="#c7d5d5"/><stop offset="1" stop-color="#66858b"/></linearGradient><linearGradient id="plumbTop" x2="1" y2="1"><stop stop-color="#d6a267"/><stop offset="1" stop-color="#8e5d3d"/></linearGradient></defs>
        <ellipse cx="209" cy="261" rx="142" ry="17" fill="#000" opacity=".28"/><path d="M72 78h111q30 0 30 30v34q0 27 27 27h100" fill="none" stroke="#31444a" stroke-width="36" stroke-linejoin="round"/><path d="M72 78h111q30 0 30 30v34q0 27 27 27h100" fill="none" stroke="url(#pipe)" stroke-width="27" stroke-linejoin="round"/><path d="M73 78h30m205 91h31" stroke="#f2dfba" stroke-width="5" stroke-linecap="round"/><path d="M173 59v37m-15-19h30" stroke="#d9b56d" stroke-width="8" stroke-linecap="round"/>
        <path d="M149 248q-1-43 17-68 20-17 49-12 29 4 43 36l7 44Z" fill="url(#plumbTop)" stroke="#f3d3a0" stroke-width="3"/><circle cx="214" cy="126" r="37" fill="#d99f70" stroke="#f4d7b5" stroke-width="3"/><path d="M177 119q7-36 41-39 34 2 43 38l-14-6-15-13-22 12-24 4Z" fill="#b97843"/><path d="M179 91q33-21 66 1" stroke="#e6bf78" stroke-width="12" stroke-linecap="round" fill="none"/><path d="m263 169 50-48" stroke="#d9a86e" stroke-width="8" stroke-linecap="round"/><path d="m308 118 25-25m-18 32 26-24" stroke="#d6d8d2" stroke-width="9" stroke-linecap="round"/>
        <path d="M335 205q-15 22 0 34 16-12 0-34Z" fill="#6fc4d1" stroke="#d2f5f4" stroke-width="2"/><path d="M362 218q-12 18 0 27 13-9 0-27Z" fill="#6fc4d1"/><circle cx="92" cy="198" r="8" fill="#88d4dd"/><path d="m92 176 4 11 11 4-11 4-4 11-4-11-11-4 11-4Z" fill="#e4d0a3"/>
      </svg>`
    },
    cleaning: {
      label: 'Cleaning professional', accent: '#a6c7b7',
      svg: `<svg viewBox="0 0 420 300" class="scene-svg" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <defs><linearGradient id="bottle" x2="1" y2="1"><stop stop-color="#b4d7cc"/><stop offset="1" stop-color="#5e9c8c"/></linearGradient><linearGradient id="cleanSuit" x2="1" y2="1"><stop stop-color="#ddb273"/><stop offset="1" stop-color="#936a45"/></linearGradient></defs>
        <ellipse cx="208" cy="261" rx="139" ry="18" fill="#000" opacity=".28"/><path d="M122 80h80l-8 158h-65Z" fill="url(#bottle)" stroke="#def0e4" stroke-width="4"/><path d="M145 80V57h35v23m-27-25h43l15 13h-57Z" fill="#e6bd73" stroke="#fff0cb" stroke-width="3"/><path d="M134 149h56v44h-56Z" fill="#355e58" opacity=".55"/><path d="M144 164h36" stroke="#e8f4eb" stroke-width="5" stroke-linecap="round"/><path d="M155 177h24" stroke="#e8f4eb" stroke-width="3" stroke-linecap="round"/>
        <path d="M238 249q-1-48 18-75 19-19 47-13 28 7 39 37l2 51Z" fill="url(#cleanSuit)" stroke="#f1d1a0" stroke-width="3"/><circle cx="288" cy="127" r="38" fill="#dba273" stroke="#f2d3ae" stroke-width="3"/><path d="M249 119q8-39 43-39 34 3 43 39l-19-8-16-13-23 13-22 3Z" fill="#8f674a"/><path d="M252 90q35-24 70 2" fill="none" stroke="#d7bd8d" stroke-width="11" stroke-linecap="round"/><path d="m318 170 30 43" stroke="#d3a16a" stroke-width="8" stroke-linecap="round"/><path d="m343 208 19 15-9 12-22-18Z" fill="#e4d1a1"/><path d="m349 230-25 18m25-18 24 18" stroke="#c9b17a" stroke-width="5" stroke-linecap="round"/>
        <path d="m75 80 5 13 13 5-13 5-5 13-5-13-13-5 13-5Z" fill="#eff7d9"/><path d="m365 91 4 10 10 4-10 4-4 10-4-10-10-4 10-4Z" fill="#eff7d9"/><circle cx="82" cy="170" r="6" fill="#9dd3bf"/><circle cx="360" cy="167" r="5" fill="#9dd3bf"/>
      </svg>`
    }
  };

  // Render local, category-specific SVG art into each scene slot.
  document.querySelectorAll('[data-service-scene]').forEach((node) => {
    const key = categoryKey(node.dataset.category || node.dataset.serviceScene);
    const scene = art[key] || art.home;
    node.dataset.sceneType = key;
    node.innerHTML = scene.svg;
    node.setAttribute('aria-label', scene.label);
  });

  // Admin dashboard: client-side filtering of the currently loaded pending applications.
  const providerFilter = document.getElementById('provider-application-filter');
  if (providerFilter) {
    const applications = Array.from(document.querySelectorAll('[data-provider-application]'));
    const noMatch = document.getElementById('provider-filter-empty');
    providerFilter.addEventListener('input', () => {
      const query = providerFilter.value.trim().toLocaleLowerCase();
      let visible = 0;
      applications.forEach((item) => {
        const match = (item.dataset.search || '').includes(query);
        item.hidden = !match;
        if (match) visible += 1;
      });
      if (noMatch) noMatch.hidden = visible !== 0;
    });
  }

  // Keep date inputs from accepting a date earlier than their minimum.
  document.querySelectorAll('input[type="date"][min]').forEach((input) => {
    input.addEventListener('change', () => {
      if (input.value && input.min && input.value < input.min) input.value = input.min;
    });
  });

  // Accessible mobile navigation.
  const toggle = document.querySelector('.nav-toggle');
  const nav = document.getElementById('primary-nav');
  if (toggle && nav) {
    const closeMenu = () => {
      nav.classList.remove('is-open');
      toggle.setAttribute('aria-expanded', 'false');
      toggle.setAttribute('aria-label', 'Open navigation');
    };
    toggle.addEventListener('click', () => {
      const isOpen = toggle.getAttribute('aria-expanded') === 'true';
      nav.classList.toggle('is-open', !isOpen);
      toggle.setAttribute('aria-expanded', String(!isOpen));
      toggle.setAttribute('aria-label', isOpen ? 'Open navigation' : 'Close navigation');
    });
    nav.querySelectorAll('a').forEach((link) => link.addEventListener('click', closeMenu));
    document.addEventListener('keydown', (event) => { if (event.key === 'Escape') closeMenu(); });
    window.addEventListener('resize', () => { if (window.innerWidth > 760) closeMenu(); });
  }

  // Scroll-in reveal: content stays visible when JS or IntersectionObserver is unavailable.
  const revealTargets = document.querySelectorAll('.section, .dark-band, .page-heading, .directory-showcase, .detail-grid, .dashboard-heading, .stat-card, .dashboard-panel, .auth-card, .form-card, .service-card, .category-card, .booking-detail-card, .table-card');
  if (revealTargets.length && !reducedMotion && 'IntersectionObserver' in window) {
    document.documentElement.classList.add('hs-motion-ready');
    revealTargets.forEach((node, index) => {
      node.classList.add('hs-reveal');
      node.style.setProperty('--reveal-delay', `${Math.min(index % 6, 5) * 55}ms`);
    });
    const observer = new IntersectionObserver((entries, obs) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible');
          obs.unobserve(entry.target);
        }
      });
    }, { threshold: 0.09, rootMargin: '0px 0px -30px 0px' });
    revealTargets.forEach((node) => observer.observe(node));
  }

  // A restrained pointer response remains on the hero scene.
  if (!reducedMotion && window.matchMedia('(hover: hover) and (pointer: fine)').matches) {
    const heroArt = document.querySelector('.hs-cinematic .hero-art');
    if (heroArt) {
      heroArt.addEventListener('pointermove', (event) => {
        const rect = heroArt.getBoundingClientRect();
        const x = (event.clientX - rect.left) / Math.max(1, rect.width) - 0.5;
        heroArt.style.setProperty('--scene-shift-x', `${x * 5}px`);
      }, { passive: true });
      heroArt.addEventListener('pointerleave', () => {
        heroArt.style.setProperty('--scene-shift-x', '0px');
      });
    }
  }

  // Reading-position progress indicator: a thin cinematic line at the top of the page.
  const progress = document.createElement('div');
  progress.className = 'hs-reading-progress';
  progress.setAttribute('aria-hidden', 'true');
  document.body.appendChild(progress);
  let scrollTicking = false;
  const updateScrollProgress = () => {
    const scrollable = document.documentElement.scrollHeight - window.innerHeight;
    const amount = scrollable > 0 ? (window.scrollY / scrollable) * 100 : 0;
    progress.style.transform = `scaleX(${Math.min(100, Math.max(0, amount)) / 100})`;
    scrollTicking = false;
  };
  window.addEventListener('scroll', () => {
    if (!scrollTicking) {
      window.requestAnimationFrame(updateScrollProgress);
      scrollTicking = true;
    }
  }, { passive: true });
  updateScrollProgress();

  // Count-up treatment for dashboard metrics, preserving the server-rendered final value.
  if (!reducedMotion && 'IntersectionObserver' in window) {
    const metricObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        const metric = entry.target;
        const raw = metric.textContent.trim();
        if (!/^\d+$/.test(raw)) { observer.unobserve(metric); return; }
        const target = Number(raw);
        const duration = 850;
        const start = performance.now();
        const tick = (now) => {
          const ratio = Math.min(1, (now - start) / duration);
          const eased = 1 - Math.pow(1 - ratio, 3);
          metric.textContent = String(Math.round(target * eased));
          if (ratio < 1) window.requestAnimationFrame(tick);
          else metric.textContent = raw;
        };
        window.requestAnimationFrame(tick);
        observer.unobserve(metric);
      });
    }, { threshold: 0.6 });
    document.querySelectorAll('.stat-card > strong').forEach((metric) => metricObserver.observe(metric));
  }

  // Cinematic interaction layer: spring-follow cursor aura and magnetic CTAs.
  // The native pointer remains visible; these effects are only a visual companion.
  const cinematicHero = document.querySelector('.hs-cinematic');
  if (cinematicHero) {
    requestAnimationFrame(() => document.documentElement.classList.add('hs-cinematic-ready'));

    const scrollCue = cinematicHero.querySelector('.hero-scroll-cue');
    if (scrollCue) {
      scrollCue.addEventListener('click', (event) => {
        const target = document.querySelector(scrollCue.getAttribute('href'));
        if (!target) return;
        event.preventDefault();
        target.scrollIntoView({ behavior: reducedMotion ? 'auto' : 'smooth', block: 'start' });
      });
    }

  }

  // Subtle header border change communicates reading position without changing layout.
  const cinematicHeader = document.querySelector('.site-header');
  if (cinematicHeader) {
    const syncHeaderDepth = () => cinematicHeader.classList.toggle('is-scrolled', window.scrollY > 24);
    syncHeaderDepth();
    window.addEventListener('scroll', syncHeaderDepth, { passive: true });
  }
});
