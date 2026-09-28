const { app, BrowserWindow, Menu, shell } = require('electron');
const path = require('path');

let win;

function goHome() {
  win.loadFile(path.join(__dirname, 'index.html'));
}

function goBack() {
  const h = win.webContents.navigationHistory;
  if (h.canGoBack()) h.goBack();
}

function createWindow() {
  win = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 800,
    minHeight: 600,
    title: 'الأدوات البيئية',
    icon: path.join(__dirname, 'icon.ico'),
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true
    }
  });

  // الروابط الخارجية تتفتح في المتصفح العادي مش جوه البرنامج
  win.webContents.setWindowOpenHandler(({ url }) => {
    if (/^https?:/i.test(url)) shell.openExternal(url);
    return { action: 'deny' };
  });
  win.webContents.on('will-navigate', (e, url) => {
    if (/^https?:/i.test(url)) {
      e.preventDefault();
      shell.openExternal(url);
    }
  });

  const menu = Menu.buildFromTemplate([
    {
      label: 'الصفحة',
      submenu: [
        { label: 'الرئيسية', accelerator: 'CmdOrCtrl+H', click: goHome },
        { label: 'رجوع', accelerator: 'Alt+Left', click: goBack },
        { type: 'separator' },
        { label: 'طباعة / حفظ كملف', accelerator: 'CmdOrCtrl+P', click: () => win.webContents.print() },
        { type: 'separator' },
        { label: 'خروج', role: 'quit' }
      ]
    },
    {
      label: 'العرض',
      submenu: [
        { label: 'تكبير', role: 'zoomIn' },
        { label: 'تصغير', role: 'zoomOut' },
        { label: 'الحجم الأصلي', role: 'resetZoom' },
        { type: 'separator' },
        { label: 'ملء الشاشة', role: 'togglefullscreen' }
      ]
    }
  ]);
  Menu.setApplicationMenu(menu);

  goHome();
}

app.whenReady().then(createWindow);
app.on('window-all-closed', () => app.quit());
