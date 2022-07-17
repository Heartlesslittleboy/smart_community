<template>
  <div class="dashboard-container">
    <div class="dashboard-text">
      <!-- name: {{ name }} -->
      <h2>智慧社区一站式解决方案</h2>
      <h5>链接物业、业主、商家，整合更多商业服务及公共服务具备成熟的运营方案支持，让智慧社区更具备运营能力，盈利能力</h5>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  computed: {
    ...mapGetters([
      'name'
    ])
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    //margin: 30px;
    background: url("../imag/banner2.jpg");
    width:100%;
    height:100%;
    position:fixed;
    background-size:100% 100%;
  }
  &-text {
    //margin: auto;
    margin-left: 300px;
    padding-top: 100px;
    width: 700px;
    text-align: center;
    font-size: 30px;
    line-height: 46px;
  }
}
h2{
  color: white;
}
h5{
  color: rgb(238, 230, 230);
}
</style>
