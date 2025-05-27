import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/app-layout/theme/lumo/vaadin-drawer-toggle.js';
import '@vaadin/side-nav/theme/lumo/vaadin-side-nav.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import '@vaadin/side-nav/theme/lumo/vaadin-side-nav-item.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/scroller/theme/lumo/vaadin-scroller.js';
import '@vaadin/app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'aa12d82bc131545745aa359188f7d014ba6e7789840b30e8e206497da088a636') {
    pending.push(import('./chunks/chunk-0cdbb1d1ee6c598ef4d9ed1adb537748c869024643c97e9a9786f4b5ab9f7e6d.js'));
  }
  if (key === 'e63f6cfff38d3690b7c6ac49c455ee4637f00599f28eedf8f478037b880cb7d1') {
    pending.push(import('./chunks/chunk-f803df643c07aa37eee57a440b68a91abb3cd3408a72bcc573024c04162131e4.js'));
  }
  if (key === '878cf3837b65c58a0fc0d3f2146fd45d0fa836838df4f43847fe8c95cca144fb') {
    pending.push(import('./chunks/chunk-f803df643c07aa37eee57a440b68a91abb3cd3408a72bcc573024c04162131e4.js'));
  }
  if (key === '25b31699979a6fc43eaaf7d39def35d0ef7160bcb71f14e5fe095f7db76b053a') {
    pending.push(import('./chunks/chunk-f803df643c07aa37eee57a440b68a91abb3cd3408a72bcc573024c04162131e4.js'));
  }
  if (key === '409c4e0eb53b0d13437b919326b14b19659c9d7fcb41b1c3174961d4bc415b47') {
    pending.push(import('./chunks/chunk-90d246ee30e3af54c28dc1be38d26613c7824df5c7d459e6a7bcdcfb75a0eb83.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}