package scripts;



public class BuyerGUI extends javax.swing.JFrame {

    /**
     * Creates new form BuyerGUI
     */
    public BuyerGUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        control_tab = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        scriptstatus_lbl = new javax.swing.JLabel();
        scriptstop_btn = new javax.swing.JButton();
        scriptstart_btn = new javax.swing.JButton();
        loadsettings = new javax.swing.JButton();
        savesettings = new javax.swing.JButton();
        breakseconds = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        set1_box = new javax.swing.JCheckBox();
        set2_box = new javax.swing.JCheckBox();
        set3_box = new javax.swing.JCheckBox();
        set4_box = new javax.swing.JCheckBox();
        set5_box = new javax.swing.JCheckBox();
        set6_box = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        worldset_lbl = new javax.swing.JTextArea();
        shoplocation = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        npcsettings_tab = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        npclist = new javax.swing.JList<>();
        npcname = new javax.swing.JTextField();
        buysettings_tab = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        npcinventory_buy = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        buy_list = new javax.swing.JList<>();
        buy_add = new javax.swing.JButton();
        buy_remove = new javax.swing.JButton();
        sellsettings_tab = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        npcinventory_sell = new javax.swing.JList<>();
        sell_add = new javax.swing.JButton();
        sell_remove = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        sell_list = new javax.swing.JList<>();
        mule_tab = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        mule_enabled = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        exchangeplace = new javax.swing.JComboBox<>();
        mule_depoitems = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        mulesetting_whengoldcheck = new javax.swing.JCheckBox();
        mulesetting_goldlessthan = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        mulesetting_goldmorethan = new javax.swing.JSpinner();
        mulesetting_ifsoldall = new javax.swing.JCheckBox();
        mulesetting_requestsell = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        mulesetting_requestsellamount = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel11.setText("Status:");

        scriptstatus_lbl.setText("Not running..");

        scriptstop_btn.setText("Stop");

        scriptstart_btn.setText("Start");

        loadsettings.setText("Load settings");

        savesettings.setText("Save settings");

        breakseconds.setModel(new javax.swing.SpinnerNumberModel(20, 1, null, 1));

        jLabel1.setText("Seconds to wait before logging back (after \"Too many login attempts\")");

        jLabel2.setText("World sets (9 worlds per set)");

        set1_box.setText("Set 1");

        set2_box.setText("Set 2");

        set3_box.setText("Set 3");

        set4_box.setText("Set 4");

        set5_box.setText("Set 5");

        set6_box.setText("Set 6");

        worldset_lbl.setColumns(20);
        worldset_lbl.setRows(5);
        worldset_lbl.setAutoscrolls(false);
        worldset_lbl.setEnabled(false);
        jScrollPane1.setViewportView(worldset_lbl);

        shoplocation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Varrock", "Rimmington", "Port Sarim" }));

        jLabel8.setText("Shop location:");

        javax.swing.GroupLayout control_tabLayout = new javax.swing.GroupLayout(control_tab);
        control_tab.setLayout(control_tabLayout);
        control_tabLayout.setHorizontalGroup(
            control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(control_tabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(control_tabLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scriptstatus_lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scriptstop_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(control_tabLayout.createSequentialGroup()
                        .addGroup(control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(control_tabLayout.createSequentialGroup()
                                .addComponent(set1_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(set2_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(set3_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(set4_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(set5_box)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(set6_box))
                            .addComponent(jLabel2)
                            .addComponent(shoplocation, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, control_tabLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scriptstart_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addComponent(loadsettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(savesettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, control_tabLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(breakseconds, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        control_tabLayout.setVerticalGroup(
            control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(control_tabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(scriptstatus_lbl))
                    .addComponent(scriptstop_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scriptstart_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(loadsettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(savesettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shoplocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(set1_box)
                    .addComponent(set2_box)
                    .addComponent(set3_box)
                    .addComponent(set4_box)
                    .addComponent(set5_box)
                    .addComponent(set6_box))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(control_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(breakseconds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Start/Stop", control_tab);

        jLabel4.setText("NPC to buy from:");

        jScrollPane2.setViewportView(npclist);

        npcname.setText("Selected:");
        npcname.setEnabled(false);

        javax.swing.GroupLayout npcsettings_tabLayout = new javax.swing.GroupLayout(npcsettings_tab);
        npcsettings_tab.setLayout(npcsettings_tabLayout);
        npcsettings_tabLayout.setHorizontalGroup(
            npcsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(npcsettings_tabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(npcsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addGroup(npcsettings_tabLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(npcname))
                .addContainerGap())
        );
        npcsettings_tabLayout.setVerticalGroup(
            npcsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(npcsettings_tabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(npcname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(304, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("NPC Selection", npcsettings_tab);

        jLabel5.setText("Items to buy: (Must have shop window opened)");

        npcinventory_buy.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(npcinventory_buy);

        buy_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(buy_list);

        buy_add.setText("Add");

        buy_remove.setText("Delete");

        javax.swing.GroupLayout buysettings_tabLayout = new javax.swing.GroupLayout(buysettings_tab);
        buysettings_tab.setLayout(buysettings_tabLayout);
        buysettings_tabLayout.setHorizontalGroup(
            buysettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buysettings_tabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buysettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buysettings_tabLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(buysettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(buy_remove, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                            .addComponent(buy_add, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buysettings_tabLayout.setVerticalGroup(
            buysettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buysettings_tabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buysettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(buysettings_tabLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(buysettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(buysettings_tabLayout.createSequentialGroup()
                                .addComponent(buy_add, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buy_remove, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Buying", buysettings_tab);

        jLabel6.setText("Items to sell: (Must have shop window opened)");

        npcinventory_sell.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(npcinventory_sell);

        sell_add.setText("Add");

        sell_remove.setText("Delete");

        sell_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(sell_list);

        javax.swing.GroupLayout sellsettings_tabLayout = new javax.swing.GroupLayout(sellsettings_tab);
        sellsettings_tab.setLayout(sellsettings_tabLayout);
        sellsettings_tabLayout.setHorizontalGroup(
            sellsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sellsettings_tabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sellsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sellsettings_tabLayout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sellsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sell_remove, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                            .addComponent(sell_add, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sellsettings_tabLayout.setVerticalGroup(
            sellsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sellsettings_tabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sellsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(sellsettings_tabLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sellsettings_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(sellsettings_tabLayout.createSequentialGroup()
                                .addComponent(sell_add, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sell_remove, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Selling", sellsettings_tab);

        jLabel7.setText("Enable dropping money to mule?");

        jLabel10.setText("Exchange place");

        exchangeplace.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Varrock square", "Grand Exchange", "Lumbridge spawn", "Edgeville Bank" }));

        mule_depoitems.setText("Deposit all bought items?");

        jLabel3.setText("When to call for deposit?");

        mulesetting_whengoldcheck.setText("When gold");

        mulesetting_goldlessthan.setModel(new javax.swing.SpinnerNumberModel(1000, 1, null, 1));

        jLabel12.setText("Is less than");

        jLabel13.setText("Is more than");

        mulesetting_goldmorethan.setModel(new javax.swing.SpinnerNumberModel(1000000, 1, null, 1));

        mulesetting_ifsoldall.setText("If run out of all of selling items");

        mulesetting_requestsell.setText("Request sell items?");

        jLabel14.setText("How many of each should we request?");

        mulesetting_requestsellamount.setModel(new javax.swing.SpinnerNumberModel(1000, 1, null, 1));

        javax.swing.GroupLayout mule_tabLayout = new javax.swing.GroupLayout(mule_tab);
        mule_tab.setLayout(mule_tabLayout);
        mule_tabLayout.setHorizontalGroup(
            mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mule_tabLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mule_tabLayout.createSequentialGroup()
                        .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mule_tabLayout.createSequentialGroup()
                                .addComponent(mulesetting_requestsell)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mulesetting_requestsellamount, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(mulesetting_ifsoldall)
                            .addComponent(jLabel3)
                            .addGroup(mule_tabLayout.createSequentialGroup()
                                .addComponent(mulesetting_whengoldcheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mulesetting_goldlessthan, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mulesetting_goldmorethan, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(mule_tabLayout.createSequentialGroup()
                        .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mule_tabLayout.createSequentialGroup()
                                .addComponent(mule_enabled)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7))
                            .addComponent(mule_depoitems))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exchangeplace, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mule_tabLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10)
                                .addGap(50, 50, 50)))
                        .addGap(81, 81, 81))))
        );
        mule_tabLayout.setVerticalGroup(
            mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mule_tabLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mule_tabLayout.createSequentialGroup()
                        .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mule_enabled, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(7, 7, 7)
                        .addComponent(mule_depoitems))
                    .addGroup(mule_tabLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exchangeplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mulesetting_goldlessthan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(mulesetting_goldmorethan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mulesetting_whengoldcheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mulesetting_ifsoldall)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mule_tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mulesetting_requestsell)
                    .addComponent(jLabel14)
                    .addComponent(mulesetting_requestsellamount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(309, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Mule Settings", mule_tab);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BuyerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuyerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuyerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuyerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BuyerGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    public javax.swing.JSpinner breakseconds;
    public javax.swing.JButton buy_add;
    public javax.swing.JList<String> buy_list;
    public javax.swing.JButton buy_remove;
    public javax.swing.JPanel buysettings_tab;
    public javax.swing.JPanel control_tab;
    public javax.swing.JComboBox<String> exchangeplace;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel10;
    public javax.swing.JLabel jLabel11;
    public javax.swing.JLabel jLabel12;
    public javax.swing.JLabel jLabel13;
    public javax.swing.JLabel jLabel14;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabel8;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JScrollPane jScrollPane5;
    public javax.swing.JScrollPane jScrollPane6;
    public javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JButton loadsettings;
    public javax.swing.JCheckBox mule_depoitems;
    public javax.swing.JCheckBox mule_enabled;
    public javax.swing.JPanel mule_tab;
    public javax.swing.JSpinner mulesetting_goldlessthan;
    public javax.swing.JSpinner mulesetting_goldmorethan;
    public javax.swing.JCheckBox mulesetting_ifsoldall;
    public javax.swing.JCheckBox mulesetting_requestsell;
    public javax.swing.JSpinner mulesetting_requestsellamount;
    public javax.swing.JCheckBox mulesetting_whengoldcheck;
    public javax.swing.JList<String> npcinventory_buy;
    public javax.swing.JList<String> npcinventory_sell;
    public javax.swing.JList<String> npclist;
    public javax.swing.JTextField npcname;
    public javax.swing.JPanel npcsettings_tab;
    public javax.swing.JButton savesettings;
    public javax.swing.JButton scriptstart_btn;
    public javax.swing.JLabel scriptstatus_lbl;
    public javax.swing.JButton scriptstop_btn;
    public javax.swing.JButton sell_add;
    public javax.swing.JList<String> sell_list;
    public javax.swing.JButton sell_remove;
    public javax.swing.JPanel sellsettings_tab;
    public javax.swing.JCheckBox set1_box;
    public javax.swing.JCheckBox set2_box;
    public javax.swing.JCheckBox set3_box;
    public javax.swing.JCheckBox set4_box;
    public javax.swing.JCheckBox set5_box;
    public javax.swing.JCheckBox set6_box;
    public javax.swing.JComboBox<String> shoplocation;
    public javax.swing.JTextArea worldset_lbl;
    // End of variables declaration                   
}