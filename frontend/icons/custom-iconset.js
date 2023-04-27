/*******************************************************************************
 * TODO: explanation what the class does
 *  
 * @author Kevin Feichtinger
 *  
 * Copyright 2023 Johannes Kepler University Linz
 * LIT Cyber-Physical Systems Lab
 * All rights reserved
 *******************************************************************************/
import '@vaadin/vaadin-icon/vaadin-iconset.js';
import '@vaadin/vaadin-icon/vaadin-icon.js';

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<vaadin-iconset name="custom" size="512">
<svg><defs>
<g id="logo"><path d="M 79.677734 160 L 192 378.54883 L 192 263.30273 L 139.74219 160 L 79.677734 160 z M 371.62695 160 L 320 262.24609 L 320 378.51367 L 432.30469 160 L 371.62695 160 z M 225.31463,287.0017 V 127.03354 H 145.33055 65.346471 V 96.190005 65.346471 H 256.15817 446.96986 V 96.190005 127.03354 H 366.98578 287.0017 V 287.0017 446.96986 h -30.84353 -30.84354 z"/></g>
</defs></svg>
</vaadin-iconset>`;

document.head.appendChild($_documentContainer.content);