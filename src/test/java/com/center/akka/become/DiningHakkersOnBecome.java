package com.center.akka.become;

import static com.center.akka.become.Messages.Eat;
import static com.center.akka.become.Messages.Think;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.FI.TypedPredicate;
import akka.japi.pf.FI.UnitApply;
import akka.japi.pf.ReceiveBuilder;

import com.center.akka.become.Messages.Busy;
import com.center.akka.become.Messages.Put;
import com.center.akka.become.Messages.Take;
import com.center.akka.become.Messages.Taken;

public class DiningHakkersOnBecome {

  /*
   * 筷子是一个Actor，它可以被拿到或者放下
   */
  public static class Chopstick extends AbstractActor {

    // 当一双筷子被一个黑客拿到后一根后，将会拒绝另外一个黑客拿另外一根，同时拥有筷子的黑客需要放回拿到的一根筷子
    PartialFunction<Object, BoxedUnit> takenBy(final ActorRef hakker) {
      return ReceiveBuilder.match(Take.class, new UnitApply<Take>() {
        public void apply(Take t) throws Exception {
          t.hakker.tell(new Busy(self()), self());
        }
      }).match(Put.class, new TypedPredicate<Put>() {
        public boolean defined(Put p) {
          return p.hakker == hakker;
        }
      }, new UnitApply<Put>() {
        public void apply(Put p) throws Exception {
          context().become(available);
        }
      }).build();
    }

    // 筷子可用状态
    PartialFunction<Object, BoxedUnit> available = ReceiveBuilder.match(Take.class, new UnitApply<Take>() {
      public void apply(Take t) throws Exception {
        context().become(takenBy(t.hakker));
        t.hakker.tell(new Taken(self()), self());
      }
    }).build();

    // 设置初始状态可用
    public Chopstick() {
      receive(available);
    }
  }

  public static class Hakker extends AbstractActor {
    private String name;
    private ActorRef left;
    private ActorRef right;

    public Hakker(final String name, ActorRef left, ActorRef right) {
      this.name = name;
      this.left = left;
      this.right = right;
      // 设置默认首先去思考
      receive(ReceiveBuilder.matchEquals(Think, new UnitApply<Object>() {
        public void apply(Object i) throws Exception {
          System.out.println(String.format("%s starts to think", name));
          startThinking(Duration.create(5, SECONDS));
        }
      }).build());
    }

    public Hakker() {
      receive(ReceiveBuilder.matchEquals(Think, new UnitApply<Object>() {
        public void apply(Object i) throws Exception {
          System.out.println(String.format("%s starts to think", name));
          startThinking(Duration.create(5, SECONDS));
        }
      }).build());
    }

    // 吃行为：当黑客吃饭时，他会决定开始思考，并且放下手中的筷子。
    PartialFunction<Object, BoxedUnit> eating = ReceiveBuilder.matchEquals(Think, new UnitApply<Object>() {
      public void apply(Object i) throws Exception {
        left.tell(new Put(self()), self());
        right.tell(new Put(self()), self());
        System.out.println(String.format("%s puts down his chopsticks and starts to think", name));
        startThinking(Duration.create(5, SECONDS));
      }
    }).build();

    // 等待另外筷子行为：当黑客等待另外一根筷子时他可以获得另外一根即可吃饭啦。如果另外一根筷子被占用，则他会放弃手中已有的筷子继续去思考怎么能拿到筷子去吃饭！！
    PartialFunction<Object, BoxedUnit> waitingFor(final ActorRef chopstickToWaitFor, final ActorRef otherChopstick) {
      return ReceiveBuilder.match(Taken.class, new TypedPredicate<Taken>() {
        public boolean defined(Taken t) {
          return t.chopstick == chopstickToWaitFor;
        }

      }, new UnitApply<Taken>() {
        public void apply(Taken i) throws Exception {
          System.out.println(String.format("%s has picked up %s and %s and starts to eat", name, left.path().name(), right.path().name()));
          context().become(eating);
          context().system().scheduler().scheduleOnce(Duration.create(5, SECONDS), self(), Think, context().system().dispatcher(), self());
        }
      }).match(Busy.class, new UnitApply<Busy>() {
        public void apply(Busy b) throws Exception {
          otherChopstick.tell(new Put(self()), self());
          startThinking(Duration.create(10, MILLISECONDS));
        }
      }).build();
    }

    // 放弃筷子行为：如果抓筷子没抓到，则继续去思考
    PartialFunction<Object, BoxedUnit> deniedAChopstick = ReceiveBuilder.match(Taken.class, new UnitApply<Taken>() {
      public void apply(Taken t) throws Exception {
        t.chopstick.tell(new Put(self()), self());
        startThinking(Duration.create(10, MILLISECONDS));
      }
    }).match(Busy.class, new UnitApply<Busy>() {
      public void apply(Busy b) throws Exception {
        startThinking(Duration.create(10, MILLISECONDS));
      }
    }).build();

    // 饿行为：当一个黑客饿的时候，他会努力捡筷子去吃饭；当他拿到一根筷子时会等待去拿另外一根筷子；如果尝试拿一根筷子失败他会等待下一次抓筷子的响应。
    PartialFunction<Object, BoxedUnit> hungry = ReceiveBuilder.match(Taken.class, new TypedPredicate<Taken>() {

      public boolean defined(Taken t) {
        return t.chopstick == left;
      }
    }, new UnitApply<Taken>() {
      public void apply(Taken t) throws Exception {
        context().become(waitingFor(right, left));
      }
    }).match(Taken.class, new TypedPredicate<Taken>() {
      public boolean defined(Taken t) {
        return t.chopstick == right;
      }
    }, new UnitApply<Taken>() {
      public void apply(Taken t) throws Exception {
        context().become(waitingFor(left, right));
      }
    }).match(Busy.class, new UnitApply<Busy>() {
      public void apply(Busy b) throws Exception {
        context().become(deniedAChopstick);
      }
    }).build();

    // 思考行为：当黑客去思考时，会饿，然后去捡筷子吃饭
    PartialFunction<Object, BoxedUnit> thinking = ReceiveBuilder.matchEquals(Eat, new UnitApply<Object>() {
      public void apply(Object i) throws Exception {
        context().become(hungry);
        left.tell(new Take(self()), self());
        right.tell(new Take(self()), self());
      }
    }

    ).build();

    private void startThinking(FiniteDuration duration) {
      // 设置当前行为是思考
      context().become(thinking);
      // 定时过一段时间后去吃饭
      context().system().scheduler().scheduleOnce(duration, self(), Eat, context().system().dispatcher(), self());
    }
  }

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create();
    // 创建5双筷子
    ActorRef[] chopsticks = new ActorRef[5];
    for (int i = 0; i < 5; i++)
      chopsticks[i] = system.actorOf(Props.create(Chopstick.class), "Chopstick" + i);

    // 创建5个黑客，并分配筷子
    List<String> names = Arrays.asList("Ghosh", "Boner", "Klang", "Krasser", "Manie");
    List<ActorRef> hakkers = new ArrayList<ActorRef>();
    int i = 0;
    for (String name : names) {
      hakkers.add(system.actorOf(Props.create(Hakker.class, name, chopsticks[i], chopsticks[(i + 1) % 5])));
      i++;
    }

    // 启动，黑客的初始状态是去思考
    for (ActorRef ar : hakkers) {
      ar.tell(Think, ActorRef.noSender());
    }

  }

}
